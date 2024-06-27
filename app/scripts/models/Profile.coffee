class mr.ProfileName

  constructor: (first, middle, last) ->

    @first = first
    @middle = middle
    @last = last

class mr.ProfileDate

  constructor: (day, month, year) ->

    @day = day
    @month = month
    @year = year

class mr.PersonalProfile

  constructor: (nin = "", gender, phone_number) ->
    @nin = nin
    @name = new mr.ProfileName()
    @birthday = new mr.ProfileDate()
    @gender = gender
    @phone_number = phone_number
    @address = new mr.Address()

class mr.CorporateProfile

  constructor: (nin = "", gender, phone_number) ->
    @nin = nin
    @name = new mr.ProfileName()
    @birthday = new mr.ProfileDate()
    @gender = gender
    @address = new mr.Address()
    @phone_number = phone_number
    @established_on = new mr.ProfileDate()
    @director_name = new mr.ProfileName()
