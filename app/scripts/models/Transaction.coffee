class mr.Source

  constructor: (address, currency, amount, counterparty) ->

    @address = address
    @maxAmount =
      currency: currency
      value: amount
    if counterparty then @maxAmount.counterparty = counterparty

class mr.Destination

  constructor: (address, currency, amount, tag, counterparty) ->

    @address = address
    @amount =
      currency: currency
      value: amount
    if tag then @tag = tag
    if counterparty then @amount.counterparty = counterparty

class mr.GatewaySpecification

  constructor: (limit, counterparty, currency, frozen, qualityIn, qualityOut, ripplingDisabled) ->

    @limit = limit
    @counterparty = counterparty
    @currency = currency
    @frozen = frozen
    @qualityIn = qualityIn
    @qualityOut = qualityOut
    @ripplingDisabled = ripplingDisabled

