class mr.Contacts

  helper = mr.Helpers.factory()
  _cURL = mr.mastergateApiUrl + 'contacts'
  _contactsStorage = []
  _instance = null

  @getInstance: () ->
    if _instance is null then _instance = new Contacts()
    else _instance

  setStorage: (contacts) ->
    _contactsStorage = contacts
    mr.walletObservable.trigger('contacts-found')

  updateStorage: (contact) ->
    index = _.findIndex(_contactsStorage, ['contact_id', contact.id])
    _contactsStorage[index] = contact
    mr.walletObservable.trigger('contacts-found')

  deleteStorage: (contactID) ->
    _contactsStorage = _.filter(_contactsStorage, (c) -> return c.contact_id != contactID)
    mr.walletObservable.trigger('contacts-found')

  getStorage: () ->
    _contactsStorage

  add: (obj, callback) =>
    # obj.account_id = auth.decodeToken(auth.getAccessToken()).sub
    helper.customForReveal { url: _cURL, type: 'POST' }, obj, (data, flag) =>
      @getMine((data, flag) => if flag then @setStorage(data.contacts))
      callback data, flag
      return

  getMine: (callback, el) ->
    helper.bubbleRequest { url: _cURL, type: 'GET' }, {}, callback, el
    return

  edit: (idContact, callback) ->
    helper.customForReveal { url: _cURL, type: 'PUT' }, idContact, callback

  delete: (idContact) ->
    helper.customReveal { url:_cURL + '/' + idContact, type: 'DELETE' }, {}, (data, flag) ->
      data
    , '#revealDeleteContact' , '#revealFail'

  deleteContact: (idContact, cb) ->
    helper.dfaultRequest { url: _cURL + '/' + idContact, type: 'DELETE' }, {}, cb
