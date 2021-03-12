# binance-private-api-kotlin

Unofficial client for the undocumented private Binance REST API. Implemented in Kotlin, usable from any JVM
language including Java.

## Problem: Incomplete Binance public API and website

You can access some of your [Binance](https://www.binance.com/en) data via the
[official REST APIs](https://github.com/binance)
(also available [for the JVM](https://github.com/binance-exchange/binance-java-api)). On their website, you can
also download some of your data as `.xlsx` or `.csv` files.

However, neither of these methods allows you to access all your data that is essential for using a third-party
tool to calculate portfolio performance or taxes. E.g., see the
[limitations listed by CoinTracking](https://cointracking.info/import/binance/index.php).

Furthermore, downloading export files from the website is very tedious, because you have to navigate through the
confusing site structure to access multiple cumbersome menus which finally allow you to download multiple export
files one by one.

## Solution: Binance private API

The [Binance website](https://www.binance.com/en) is a
[single-page application](<https://en.wikipedia.org/wiki/Single-page_application>) that uses a REST API
different to the official public REST API. The endpoint URLs contain the substring `private`. Using a browser's
developer tools, they could be reverse-engineered to create this project, an incomplete implementation of this
private API. The used programming language is
[Kotlin](https://kotlinlang.org/), a relatively new, very readable and clean language that can run on the JVM (
Java Virtual Machine) platform. Therefore, it can be used from Kotlin, Java and other JVM languages.

## Extras

In addition to the basic implementation of the private REST API, this project also offers some extras that make
it much easier to use it:

- Most API endpoints limit the time range of a query to a three months range.
  `BinancePrivateApiFacade` transparently splits a a query with a large time range into multiple queries.
- Most API endpoints limit the number of items (e.g. `Trade`) in a result.
  `BinancePrivateApiFacade` transparently takes care of paging.
- Extension functions allow you to derive computed properties from result items
  that are not explicitly contained.
- You can create reports that are based on your current held asset quantities
  (`AssetQuantitiesReport`) and based on history (`AssetQuantitiesReport`).

## Usage

```kotlin
val curlAddressPosix = "..." // See below
val client = BinancePrivateApiRestClientFactory.newInstance(curlAddressPosix).newRestClient()
val facade = BinancePrivateApiFacade(client)
val startTime = 
    LocalDate.of(2020, 1, 1)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .truncatedTo(ChronoUnit.MILLIS)

// Get all trades since startTime from spot, cross margin and isolated margin accounts:
val trades = facade.getTradeHistory(startTime)
```

### curlAddressPosix

Since there is no API key for the private API, we need to use the same mechanism for authentication as the
Binance website, which is cookies. You need to obtain them in the following way:

- In your web browser, open the developer tools (see
  [instructions](https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_are_browser_developer_tools))
- In the browser address bar, navigate to the Binance website and log in.
- In the developer tools area, open the tab called "network" (or similar).
- Right-click an item (a network request) with host `www.binance.com` and select "Copy as cURL (POSIX)"
  (or similar).

#### Important

Note that the string you have just copied has all the confidential information that is needed to perform most
actions in your Binance account until the session expires, including trading.
Therefore, you must treat it with about the same care as a password or the official public API's key and secret!
You can easily verify that my code does nothing malicious or risky with your data.
Also, given my GitHub account, it is easy to find out my identity.
But be careful in case of a fork of this project!

## Throttling

Please use this client responsibly and do not flood the Binance server with requests! You will have to deal with
errors if you do it anyway. By default, the client waits one second after each request. You can configure the
wait period if you want to.

## Timestamp and time interval peculiarities

Regarding timestamps of result items (e.g. `IsolatedMarginBorrowing.timestamp`) and `startTime`/`endTime` of
queries, the Binance private REST API is very inconsistent, unfortunately.
`BinancePrivateApiFacade` handles most of these issues transparently for you, but you should still be aware
of them, especially if you want to understand the source code.

<table>
  <thead>
    <tr>
      <th>Issue</th>
      <th>Consequence</th>
    </tr>
  </thead>
  <tdata>
    <tr>
      <td>
        A result item's timestamp is originally known by many names in the REST API:
        <code>timestamp</code>, <code>time</code>, <code>operateTime</code> etc.
      </td>
      <td>
        This client names all these parameters <code>timestamp</code>,
        handling translation from the original name in JSON transparantly.
      </td>
    </tr>
    <tr>
      <td>
        For some endpoints, <code>startTime</code> and <code>endTime</code> of the query seem to have
        no relation to the <code>timestamp</code> of a result item.
        It seems that the Binance server uses an internal timestamp for its queries which is not exported
        via the REST API.
      </td>
      <td>
        Do not be confused if
        <code>timestamp</code> < <code>startTime</code> or <code>endTime</code> < <code>timestamp</code>.
      </td>
    </tr>
    <tr>
      <td><code>endTime</code> is inclusive for some queries and exclusive for others.</td>
      <td>
        <ul>
          <li>
            For every <code>endTime</code> query parameter, the parameter documentation tells you whether it is
            inclusive or exclusive.
          </li>
          <li>
            Additionally, there are query constructor overloads that take a
            <a href="https://guava.dev/releases/19.0/api/docs/com/google/common/collect/Range.html">
              <code>Range&lt;Instant&gt;</code>
            </a>
            as parameter that will be translated to <code>startTime</code> and <code>endTime</code>.
          </li>
          <li>
            When <code>BinancePrivateApiFacade</code> transparently handles time interval splitting,
            inclusiveness/exclusiveness is respected.
          </li>
        </ul>
      </td>
    </tr>
    <tr>
      <td>
        For different queries and the resulting items, <code>startTime</code>, <code>endTime</code>
        and <code>timestamp</code> have different resolution.
        Usually, the resulution is one millisecond, but sometimes it is one second.
      </td>
      <td>
      <ul>
          <li>
             If you try to build a query with a <code>startTime</code>/<code>endTime</code> resolution that is
            too high, you will get an exception. This prevents results that may have been confusing for you.
          </li>
          <li>
            When <code>BinancePrivateApiFacade</code> transparently handles time interval splitting,
            the resolution is respected.
          </li>
        </ul>
      </td>
    </tr>
  </tdata>
</table>

## Disclaimer and limitations

- Since this project is unofficial, you must take great care before using it to support financial decisions!
  I have taken great care because this project is crucial for my own finances, but you still
  use it at your own risk.
- Since the underlying private REST API is unofficial, it could be changed without notice,
  rendering this client broken.
  However, this is very unlikely, since the endpoints are apparently versioned,
  e.g. `https://www.binance.com/gateway-api/v3/private/...`.
- Since there is no documentation, there could be bugs due to misunderstanding of the private REST API.
- Since there is no documentation, it has been possible to use enum types for some properties only. Whenever the
  set of possible values has been unknown, `String` had to be used.
- The API has only been implemented incompletely.
  This is not due to technical restrictions, but due to restrictions of my personal requirements and time.
  Areas that are covered well (but not necessarily completely) include:
  - Payments (aka "buy crypto")
  - Distribution
  - Spot account
  - Isolated margin account (cross margin account is partially supported)
  - Flexible savings
  - Locked staking
- I have observed intermittent bugs on the official Binance website occasionally.
  I cannot tell whether these stem from bugs in their web client or in their private REST API.
  This means when using this project, you could observe bugs that are not within my realm of responsibility
  at all.
- The reports may return incorrect numbers. The current quantities should be consistent with the history.
  However, for my account, for a very small fraction of my assets, they differ. Assuming Binance did not just
  deposit or withdraw assets from my account without properly recording these transaction in history, there must
  be some error.

If you want to help with these issues, feel free to contact me or create a pull request.

## Development

Tests expects to find the file `config.properties` in the working directory. You can
use `config.properties.template` as a template and rename it. It expects the following properties:

- `curlAddressPosix`
- `apiKey`, `secret`\
  These are the same values that you would use for the official REST API. They are needed because for some
  extras, the official Java client is utilized internally.
- `accountStartYear`, `accountStartMonth`\
  The year and month of your first transactions in your Binance account.

### Roadmap

- Examine why reports are not 100% consistent. This will probably involve Binance support.
- CSV export
