-- Compensation for reserve_stock.lua: gives back the stock of an order that can no longer be relayed.
--
-- KEYS[i] = stock::{campaignId}:{variantId}
-- ARGV[i] = quantity to give back
--
-- returns { new1, new2, ... }, new i being the stock left of KEYS[i] after the release
--         -1 in that slot means the key is gone and was skipped on purpose: a blind INCRBY would
--            recreate it with a wrong absolute value and no TTL
--         -2 in that slot means the stored value is not a number and was skipped
--
-- Unlike the reserve, this runs per item best effort. One lost key must not stop the other items
-- from being released.
local itemCount = #KEYS
local result = {}

for i = 1, itemCount do
    local currentStock = redis.call('GET', KEYS[i])
    if not currentStock then
        result[i] = -1
    elseif tonumber(currentStock) == nil then
        result[i] = -2
    else
        result[i] = redis.call('INCRBY', KEYS[i], ARGV[i])
    end
end

return result
