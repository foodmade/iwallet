local lastTimeBlockKey = '_LAST_TIME_BLOCK_NUMBER'
local basisBlockKey    = '_BASIS_BLOCK_NUMBER'
local interval         = tonumber(ARGV[1])
local currentBlock     = tonumber(ARGV[2])
local blockNumber      = redis.call('GET',lastTimeBlockKey)
if blockNumber
then
    local resultBlock  = tonumber(blockNumber) + interval
    if resultBlock > currentBlock
        then
            return tonumber(blockNumber)
    end
    redis.call('SET',lastTimeBlockKey,resultBlock)
    return tonumber(blockNumber)
else
    blockNumber = redis.call('GET',basisBlockKey)
    redis.call('SET',lastTimeBlockKey,tonumber(blockNumber) + interval)
    return tonumber(blockNumber)
end

