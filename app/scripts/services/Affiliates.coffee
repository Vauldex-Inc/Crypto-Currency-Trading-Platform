class mr.Affiliates

  _instance = null
  helper = mr.Helpers.factory()
  _cURL = mr.mastergateApiUrl + 'affiliates/'

  @isAffiliateEnabled = false

  @getInstance: ->
    if _instance is null then _instance = new Affiliates()
    else _instance

  add: (id, obj) ->
    helper.promiseRequest { url: _cURL + id, type: 'POST' }, obj

  getStatus: (callback) ->
    path = 'me/status'

    helper.dfaultRequest { url: _cURL + path, type: 'GET' }, {}, (data, flag) ->
      if flag
        callback data.is_affiliate_enabled, true
      else
        callback data, false

  setStatus: (flag, callback) ->
    path = 'me/setting/status'

    helper.customForReveal { url: _cURL + path, type: 'PATCH' }, { is_enabled: flag }, callback

  unsetStatus: (flag, callback) ->
    path = 'me/setting/status'

    helper.dfaultRequest { url: _cURL + path, type: 'PATCH' }, { is_enabled: flag }, callback

  getAffiliates: (callback, el) ->
    path = 'me'

    helper.bubbleRequest { url: _cURL + path, type: 'GET' }, {}, callback, el

  claimReward: (obj, callback) ->
    path = 'me/'+obj.id

    helper.revealRequest { url: _cURL + path, type: 'PATCH' }, { 'address': obj.address }, callback

