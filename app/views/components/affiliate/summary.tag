<affiliate-summary>
  <section class="small-12 columns" if={ isEnable && level }>
    <header>
      <h2>{ $m('p.affiliate.ClaimReward')}</h2>
    </header>
    <ul class="reset" data-tour="rewardLists">
      <li if={ _.isEmpty(affiliates) }>
        <div class="panel shin-panel" data-loading="affiliate">
            <p>{ messages }</p>
          </div>
      </li>
      <li each={ affiliate in affiliates } class="panel amount-panel">
        <div class="row">
          <div class="columns">
            <p>{ affiliate.account_id }</p>
            <p class="sub">{
              $m('p.affiliate.ClaimReward',
                  moment.unix(affiliate.joined_at).format('YYYY-MM-DD hh:mm:ss a'),
                  rewardAmount + ' '+rewardCurrency
                )}
            </p>
          </div>
          <div class="shrink columns">
            <a
              class="button success"
              onclick="{ onSelectClaim.bind(this, '#confirmClaim') }"
              data-tour="claimRewards">
              { $m('p.affiliate.ClaimReward') }
            </a>
          </div>
      </li>
    </ul>
  </section>

  <reveal-with-form
    idreveal="confirmClaim"
    heading={ $m('p.affiliate.confirmReward') }
    idform="confirmClaim"
    dataclose="false"
    size="small"
    onsubmit={ onClaimReward }>

    <yield to="content">
      <div class="reveal-body">
        <p>{ $m('p.affiliate.Claim.desc.1', " ") }</p>
        <p class="strong">{ parent.claimAccount.account_id }</p>
        <p>{ $m('p.affiliate.desc2') }</p>
        <label for="desitation" class="text-center">{ $m('p.common.WalletX', $m('p.label.Address')) }
          <select name="destination" onchange={ parent.onSelectWallet }>
            <option selected disabled hidden>{ $m('p.tour.searchWallet.h') }</option>
            <option each={ wallet in tradeWallets }>
              { wallet.name }
            </option>
          </select>
        </label>
      </div>
      <div class="reveal-text">
        <p>{ $m('p.affiliate.desc1') }</p>
      </div>
    </yield>
    <yield to="footer">
      <footer class="button-group expanded">
        <a class="button black" data-close onclick="">{ $m('p.common.Cancel') }</a>
        <button
          type="submit"
          class="btn-submit button success"
          disabled={ _.isEmpty(parent.claimAccount.address) ? 'disabled' : ''}>
          { $m('p.affiliate.Claim') }
        </button>
      </footer>
  </reveal-with-form>

  <script>
    var self = this,
        helper = mr.Helpers.factory(),
        affiliate = mr.Affiliates.getInstance(),
        wallet = mr.Wallet.getInstance(),
        currencies = mr.Currencies.getInstance(),
        accounts = mr.Accounts.getInstance(),
        paginate = this.tags.paginate

    this.affiliates = []
    this.claimAccount = {}
    this.tradeWallets = {}
    this.messages = null
    this.rewardAmount = null
    this.rewardCurrency = null
    this.isLevelMax = false
    this.isEnable = mr.Accounts.isAffiliateEnabled
    this.tourContent =[]
    this.tourOptions = []

    onLoadData() {
      affiliate.getAffiliates(function(data, flag) {
        if (data.affiliates.length) {
          var list = _.filter(data.affiliates, ['distributed_at', null] )
          self.update({ affiliates: list, rewardAmount: data.reward })
        } else
          self.update({ messages: $m('p.error.NoRewards') })
      }, '[data-loading=affiliate]')
    }

    onLoadWallets() {
      wallet.getWalletBalances().then(function() {
        self.update({ tradeWallets: mr.app.wallet.tradeWalletInstances })
      })
    }

    onLoadRewardCurrency(code) {
      currencies.getCurrencyByCode(code, function(data){
        self.update({ rewardCurrency: data.currency_code})
      })
    }

    onSelectClaim(el, ev) {
      self.update({ claimAccount: ev.item.affiliate })
      self.onReveal(el, false)
    }

    onSelectWallet(ev) {
      address = _.find(self.tradeWallets, {
        'name': ev.target.value
      }).wallet.address

      self.claimAccount.address = address
      self.update()
    }

    onClaimReward(ev) {
      ev.preventDefault()
      helper.passwordConfirmFunction(function() {
        affiliate.claimReward(self.claimAccount, function(data, flag) {
          if(flag) {
            self.affiliates = _.filter(self.affiliates, function(value, index) {
              return value.account_id != self.claimAccount.account_id
            })
            self.update()
          }
        })
      })
    }

    onReveal(el, isPassword) {
      if (isPassword) helper.revealPassword(el)
      else helper.reveal(el)
    }

    loadTour() {
      var tourContent = [{
        sel: $('[data-tour="rewardLists"]'),
        header: $m('p.affiliate.overallSummary'),
        content : $m('p.tour.overallSummary.c'),
        expose: true,
        position: 's'
      }]

      _.forEach(tourContent, function(data) {
        self.tourContent.push(data)
      })
    }

    $('#tour').click(function() {
      if (self.affiliates.length > 0)
        self.loadTour()
      new Trip(self.tourContent, self.tourOptions).start()
    })

    this.on('mount', function() {
      helper.setTitle({ title: 'Affiliate', sub: 'Summary' })

      accounts.isLevelAllowed(8, function(isAllowed) {
        if (!mr.Accounts.isAffiliateEnabled)
          helper.pushState('#content-body', 'unauthorized-affiliate')
        else
          helper.pushState('#content-body', 'unauthorized-level')
        self.update({ isLevelMax: isAllowed })
      })

      mr.walletObservable.on('affiliate-status', function() {
        self.update()
      })

      mr.walletObservable.on('level-update', function() {
        self.update()
      })

      self.onLoadData()
      self.onLoadWallets()
      self.onLoadRewardCurrency('XRP')

      var tourContent = [{
        header: $m('p.tour.welcomeSettingsTour.h', $m('p.common.Summary')),
        content: $m('p.tour.welcomeSettingsTour.c', $m('p.tour.summarySettingsTour.c')),
        expose: true,
        position: 'screen-center'
      }],
        tourOptions = {
          tripTheme: 'white',
          backToTopWhenEnded: true,
          bindKeyEvents: true,
          showHeader: true,
          showNavigation: true,
          showCloseBox: true,
          showSteps: true,
          prevLabel: $m('p.common.Back'),
          nextLabel: $m('p.common.Next'),
          finishLabel: $m('p.common.Done'),
          skipLabel: $m('p.common.Cancel'),
          delay: -1
        }

      self.update({ tourContent: tourContent, tourOptions: tourOptions })

    })

    this.on('before-unmount', function() {
      mr.walletObservable.trigger('mount-tag')
    })
  </script>
</affiliate-summary>
