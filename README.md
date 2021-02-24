# binance-private-api-kotlin

Unofficial client for the undocumented private Binance REST API. Implemented in Kotlin,
usable from any JVM language including Java.

## Problem: Incomplete Binance public API and website

You can access some of your [Binance](https://www.binance.com/en) data via the
[official REST APIs](https://github.com/binance)
(also available [for the JVM](https://github.com/binance-exchange/binance-java-api)).
On their website, you can also download some of your data as `.xlsx` or `.csv` files.

However, neither of these methods allows you to access all your data that is essential for using a
third-party tool to calculate portfolio performance or taxes. E.g., see the
[limitations listed by CoinTracking](https://cointracking.info/import/binance/index.php).

Furthermore, downloading export files from the website
is very tedious, because you have to navigate through the confusing site structure to access multiple
cumbersome menus which finally allow you to download multiple export files one by one.

## Solution: Binance private API

The [Binance website](https://www.binance.com/en) is a
[single-page application](<https://en.wikipedia.org/wiki/Single-page_application>) that uses a REST API
different to the official public REST API. The endpoint URLs contain the substring `private`.
Using a browser's developer tools, they could be reverse-engineered to create this project, an incomplete
implementation of this private API. The used programming language is
[Kotlin](https://kotlinlang.org/), a relatively new, very readable and clean language that can run
on the JVM (Java Virtual Machine) platform.
Therefore, it can be used from Kotlin, Java and other JVM languages.

## Extras

In addition to the basic implementation of the private REST API, this project also offers some extras that
make it much easier to use it:

- Most API endpoints limit the time range of a query to three months.
  The extras transparantly split a larger time range into multiple queries.
- Most API endpoints limit the number of items (e.g. trades) in a result.
  The extras transparently take care of paging.
- The extras allow you to derive properties from result items that are not explicitly contained.
- You can create reports that are based on your current held asset quantities
  (`AssetQuantitiesReport`) and based on history (`AssetQuantitiesReport`).

## Disclaimer and limitations

- Since this API is unofficial, you must take great care before using it to support financial decisions!
- Since there is no official documentation, there can be bugs due to misunderstanding of the API.
  Particularly, it seems that start and end times are interpreted differently by different endpoints.
  Sometimes, the end time seems to be inclusive (i.e. a right-closed range),
  sometimes exclusive (i.e. a right-open range).
  In other cases, data with timestamps outside of the given range was returned,
  possibly to yet-to-be-understood rounding.
  These issues have only been discovered and mitigated partially.
- Since this API is unofficial, it could be changed without notice, rendering the client obsolete or erroneous.
  However, this is very unlikely, since the private endpoints are apparantly versioned,
  e.g. `https://www.binance.com/gateway-api/v3/private/...`.
- Since there is no documentation, it has been possible to use enum types for some properties only.
  Whenever the set of possible values has been unknown, `String` had to be used.
- The API has only been implemented incompletely.
  Areas that are covered well (but not necessarily completely) include:
  - Payments (aka "buy crypto")
  - Distribution
  - Spot account
  - Isolated margin account (cross margin account is partially supported)
  - Flexible savings
  - Locked staking
- The reports may return incorrect numbers. The current quantities should be consistent with the history.
  However, for my account, for a very small fraction of my assets, they differ.
  Assuming Binance did not just deposit or withdraw assets from my account without properly recording
  these transaction in history, there must be some error.

If you want to help with these issues, feel free to contact me or create a pull request.

## Throttling

Please use this client responsibly and do not flood the Binance server with requests! You will have to deal
with errors if you do it anyway. By default, the client waits one second after each request.
You can configure the wait period if you want to.

## Usage

```kotlin
val curlAddressPosix = "..." // See below
val client = BinancePrivateApiRestClientFactory.newInstance(curlAddressPosix).newRestClient()
val startTime = 
    LocalDate.of(2020, 1, 1)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .truncatedTo(ChronoUnit.MILLIS)

// Get all trades since startTime from spot, cross margin and isolated margin accounts:
val trades = client.getTradeHistory(startTime)
```

### `curlAddressPosix`

Since there is no API key for the private API, we need to use the same mechanism for authentication as
the Binance website, which is cookies. You need to obtain them in the following way:

- In you browser, open the developer tools (see
  [instructions](https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_are_browser_developer_tools))
- In the browser address bar, navigate to the Binance website and log in.
- In the developer tools area, open the tab called "network" (or similar).
- Right-click an item (a network request) with host `www.binance.com` and select "Copy as cURL (POSIX)"
  (or similar).
- Note that the string you have just copied has all the confidential information that is needed to perform
  most actions in you Binance account until the session expires. Therefore, you must treat it with about
  the same care as a password or the official public API's key and secret!

## Development

Tests expects to find the file `config.properties` in the working directory.
You can use `config.properties.template` as a template and rename it. It expectes the following properties:

- `curlAddressPosix`
- `apiKey`, `secret`\
  These are the same values that you would use for the official REST API. They are needed because for some
  extras, the official Java client is utilized internally.
- `accountStartYear`, `accountStartMonth`\
  The year and month of your first transactions in your Binance account.

### Roadmap

- Examine time interval semantics more closely to more reliably prevent missing and duplicate result items.
- CSV export
