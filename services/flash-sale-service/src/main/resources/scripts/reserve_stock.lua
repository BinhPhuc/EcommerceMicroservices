-- Reserves flash sale stock and appends the outbox record in one atomic step.
--
-- KEYS[1]      = orderRequest::{requestId}, the idempotency key
-- KEYS[2]      = the order outbox stream
-- KEYS[3..n]   = stock::{campaignId}:{variantId}, one per ordered item
-- ARGV[1]      = FlashSaleOrderCreatedEvent serialized as JSON
-- ARGV[2]      = TTL in seconds of the idempotency key
-- ARGV[3]      = MAXLEN ~ of the outbox stream
-- ARGV[4]      = field name holding the payload inside the stream entry
-- ARGV[5..n]   = quantity to reserve, in the same order as KEYS[3..n]
--
-- returns { 0, remain1, remain2, ... } on success, remain i being the stock left of KEYS[i + 2]
--         { -1, i } stock key is missing, not pre-warmed or already expired
--         { -2, i } not enough stock
--         { -3, i } stored value is not a number
--         { -4, 0 } the request id was already reserved
local itemCount = #KEYS - 2
local requestKey = KEYS[1]
local outboxStreamKey = KEYS[2]
local payload = ARGV[1]
local requestTtlSeconds = ARGV[2]
local outboxMaxLength = ARGV[3]
local outboxPayloadField = ARGV[4]

if redis.call('EXISTS', requestKey) == 1 then
    return { -4, 0 }
end

-- Validate every item before touching any of them, so a multi item order is all or nothing.
for i = 1, itemCount do
    local currentStock = redis.call('GET', KEYS[i + 2])
    if not currentStock then
        return { -1, i }
    end
    local currentStockNumber = tonumber(currentStock)
    if currentStockNumber == nil then
        return { -3, i }
    end
    if currentStockNumber < tonumber(ARGV[i + 4]) then
        return { -2, i }
    end
end

-- DECRBY keeps the TTL that the pre-warm step put on the key.
local result = { 0 }
for i = 1, itemCount do
    result[i + 1] = redis.call('DECRBY', KEYS[i + 2], ARGV[i + 4])
end

-- Same script as the decrements above, so stock and outbox can never disagree.
redis.call('SET', requestKey, '1', 'EX', requestTtlSeconds)
redis.call('XADD', outboxStreamKey, 'MAXLEN', '~', outboxMaxLength, '*', outboxPayloadField, payload)

return result
