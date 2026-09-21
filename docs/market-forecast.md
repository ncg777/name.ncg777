# Swing market forecasting

Launch `name.ncg777.maths.neural.apps.MarketForecastApp` using the project's jar with dependencies, or choose **Market forecast** in the main launcher. Java 17+ is required. No new Maven dependencies are added: networking uses the JDK, parsing uses existing Jackson, and charts use Swing/Java2D.

```text
java -Xmx512m -cp target/name.ncg777-20260502T1743Z-jar-with-dependencies.jar name.ncg777.maths.neural.apps.MarketForecastApp
```

## Using the app

Select a source, symbol and bar size, then press **Refresh & train**. Yahoo permits typed symbols such as SPY, AAPL, ^GSPC, ^IXIC and BTC-USD. FRED offers NASDAQCOM, SP500 and DJIA as daily series. Intraday intervals are 1 minute, 5 minutes, 15 minutes and 1 hour; daily, weekly and monthly bars are also supported by Yahoo. Availability and history depend on the symbol and provider.

The price chart shows observations in cyan and nine two-bar categorical scenarios in gold. Hover over history for timestamp, value and completion status; use the chart-bar slider to change the visible range. The lower charts show probabilities of down, little change and up for each of the next two observed bars. The table shows all nine joint outcomes. This forecasts observed-bar sequence, not calendar-clock time through market closures.

Set automatic refresh to 30 seconds, 1 minute or 5 minutes. Requests never overlap. Downloading and training run outside Swing's event thread; Cancel interrupts both. Refreshes retrain from scratch on a bounded rolling history only when completed observations or training settings change. Otherwise the forecast is reused and the chart refreshed. This is periodic retraining, not an online optimizer or a streaming tick subscription.

**Open CSV** accepts simple unquoted or quoted-field Date,Close data, or ISO-8601 UTC timestamps. Adjusted Close/Adj Close/adjusted_close is preferred if present. A FRED observation_date,value pair is accepted. Empty, dot and null prices are omitted; invalid prices, duplicate timestamps and malformed rows are rejected. Do not forward-fill missing sessions. Imported data must already have the selected bar size; the app does not resample it. CSV import turns off automatic network refresh. Pressing Refresh thereafter retrains the imported data; change symbol/source to return to network data.

## Forecast construction and evaluation

The most recent 150–1,200 completed prices yield consecutive log returns. Returns are partitioned chronologically 70%/15%/15% **before** constructing eight-return windows. No return is shared across the training, validation and test partitions. Adjacent windows overlap within a partition; their scores are not independent statistical trials.

The neutral threshold is the training-only one-third quantile of absolute returns (floor 1e-8). Returns below negative threshold are -1, above positive threshold +1, otherwise zero. Zero does not mean missing. The eight-node network represents six past digits and two future digits. Forecasts enumerate and normalize just the nine endings with all six observed digits fixed.

Training uses the existing exact ternary trainer, initialized with smoothed independent marginals (0.5 pseudocounts; parameters bounded at 5). The best training checkpoint is selected by validation joint likelihood, as implemented in the trainer. That candidate is then compared with the independent initialization on validation **conditional two-bar log loss**. The better candidate supplies the displayed forecast. Test conditional log loss for both is shown separately, never used for selection. The tooltip provides validation scores and split sizes. Lower log loss is better; it measures probability quality, not trading profitability or calibration by itself.

Training is limited to 40 epochs, eight nodes and the unchanged 100-million-work-unit guard. At most 1,200 completed bars enter a fit. The latest six return digits are used for the live forecast; validation and test data are not subsequently folded into training. Thus the displayed evaluation remains tied to the actual fitted model, at the cost of not fitting the latest holdout observations. Repeatedly watching refreshed evaluation or changing settings after inspecting it is not a pristine untouched experiment. A research backtest needs a separately reserved final period and additional baselines.

For each of the three categories, a representative log return is its training-only mean. Gold scenario lines apply these means for two future bars. They omit within-category magnitude uncertainty, tails and gaps: **they are not confidence intervals, price targets, or upper/lower bounds**. The probability forecasts concern the categories only.

## Feed behavior and limitations

- [Yahoo exchange delays](https://help.yahoo.com/kb/SLN2310.html): free chart data may be delayed and varies by exchange. The app uses an unofficial chart endpoint, which may be throttled or changed without notice. It does not bypass authentication or provider errors. Adjusted close is used for daily-or-longer history when supplied; intraday uses close. The selected field is identified in the source label. Data is not saved or redistributed by the app.
- [FRED NASDAQCOM](https://fred.stlouisfed.org/series/NASDAQCOM) and [SP500](https://fred.stlouisfed.org/series/SP500): daily published index levels, not intraday streams. SP500 is a price index, excluding dividends. Series licensing remains applicable; free access is not redistribution permission.
- A bar is conservatively considered complete only after its entire nominal interval has elapsed from its supplied timestamp (one calendar month for monthly data). This can delay daily/weekly eligibility beyond the actual session close. In-progress bars may appear in the chart but never in model fitting or forecast context. The forecast anchor date is displayed explicitly. Daily-or-longer dates display in UTC; intraday times use the computer's timezone.
- Each remote request has a 25-second overall deadline and an 8 MB receiving limit. No silent fallback substitutes another symbol or data source. An error leaves previously displayed data visible with its timestamps and an error status. FRED and Yahoo availability must be tested on the deployment network.
- There is no brokerage connection, order execution, trading signal policy, transaction-cost model or claim of forecast advantage. Corporate actions, revisions, historical availability, market regime changes and costs require separate treatment before financial use.

Tests cover incomplete-bar exclusion, CSV and JSON parsing, chronological window boundaries, test-data isolation, bounded work, joint conditional probabilities and training interruption.

## U.S. and Canadian symbol screener

Open **Symbols & screener…**, then **Load / refresh symbols**. Search company names or quote symbols and filter U.S./Canada. Sources are Nasdaq Trader's Nasdaq/other exchange directories, TMX's TSX/TSXV instrument directories, and CSE's listed-company directory. Coverage excludes OTC and Cboe Canada: this is not a complete North American security master. Directory errors and fetch time remain visible. Test issues are excluded; inactive CSE listings remain searchable but are skipped during scans. All TMX instruments are expanded, including multiple share classes/currencies. Yahoo symbol conversion may fail for unusual securities; failures remain visible.

Use **Scan selected** for selected rows, or **Scan filtered** for every matching listing. Each requests daily prices and fits up to 800 completed bars, with 5 training passes by default (1–40). Requests are sequential, spaced by 1.5 seconds; broad scans can take hours. **Pause** finishes the current symbol; **Resume** continues the same queue and settings in memory. Closing the dialog cancels work. HTTP 403/429 stops the scan; wait before resuming. There is no persistent scan cache or automatic bulk polling. Opening the screener disables chart auto-refresh. New scans replace previous results.

Rank by estimated probability of positive total return over one or two trading days, before costs. Training windows estimate actual positive one-day and summed two-day log returns within each of the nine future-state pairs, shrunk toward the training-only unconditional frequency with five pseudo-observations. The joint forecast weights these rates. This distinguishes gains from the ternary up category, including neutral and mixed-sign scenarios.

Two-day held-out Brier error (mean squared probability error) is compared with a training-frequency baseline; lower is better. Model selection remains validation-only. Windows overlap within each split, and probabilities are not calibrated across symbols. Rankings are exploratory and do not establish profitable prediction. Current directories have survivorship bias and are not historical point-in-time universes. Prices older than seven calendar days are rejected; successful rows show their price date. Rankings include only successfully scanned listings; attempted/total/failure counts remain visible and failures have no score. Select a result and **Open chart** to inspect it.

Sources: [Nasdaq directory definitions](https://nasdaqtrader.com/Trader.aspx?id=SymbolDirDefs), [TMX directory](https://www.tsx.com/en/listings/listing-with-us/listed-company-directory), [CSE directory](https://thecse.com/listing/listed-companies/). No new dependency or paid subscription is required.
