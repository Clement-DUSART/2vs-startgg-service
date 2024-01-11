const apiHeader = {
      'Authorization': 'Bearer <start.gg API token>' // Api key de start.gg
  }

const baseUrl = "API url>"

// Les cellules à lire
const playersInputCell = 'B2'
const eventIdsInputCell = 'B7'
const bracketEventInputCell = 'B12' //
const bracketGroupIdentifierInputCell = 'B13'
const streamQueueEventInputCell = 'B18'
const streamQueueStreamNameInputCell = 'B19'

// Le nom des pages
const playersPage = 'players'
const eventIdsPage = 'events'
const bracketsPage = 'brackets'
const streamQueuePage = 'streams'

function getCellValue(cell) {
  return SpreadsheetApp.getActive().getRange(cell).getValue()
}

function makeApiCall(url) {
  console.log(url)
    const options = {
    'method': 'get',
    'headers': apiHeader
  }
  return JSON.parse(UrlFetchApp.fetch(url, options).getContentText())
}

function clearPage(sheet) {
  const lastRow = sheet.getLastRow()
  if (lastRow > 1) {
    var range = sheet.getRange(2, 1, lastRow - 1, sheet.getLastColumn())
    range.clearContent()
  }
}

function getPlayers() {
  const completeUrl = baseUrl + "players?tournament=" + getCellValue(playersInputCell)
  const dataSet = makeApiCall(completeUrl)
  const sheet = SpreadsheetApp.getActive().getSheetByName(playersPage)

  var rows = []

  for (i = 0; i < dataSet.length; i++) {
    const data = dataSet[i];
    rows.push([data.gamerTag, data.prefix, data.country, data.city, data.twitter, data.twitch]);
  }

  clearPage(sheet)
  const dataRange = sheet.getRange(2, 1, rows.length, 6)
  dataRange.setValues(rows);
}

function getEventIds() {
  const completeUrl = baseUrl + "eventIds?tournament=" + getCellValue(eventIdsInputCell)
  const dataSet = makeApiCall(completeUrl)
  const sheet = SpreadsheetApp.getActive().getSheetByName(eventIdsPage)

  var rows = []

  for (i = 0; i < dataSet.length; i++) {
    const data = dataSet[i];
    rows.push([data.id, data.name]);
  }

  clearPage(sheet)
  const dataRange = sheet.getRange(2, 1, rows.length, 2)
  dataRange.setValues(rows);
}

function getBrackets() {
  const completeUrl = baseUrl + "events?eventId=" + getCellValue(bracketEventInputCell) + "&groupIdentifier=" + getCellValue(bracketGroupIdentifierInputCell)
  const dataSet = makeApiCall(completeUrl)
  const sheet = SpreadsheetApp.getActive().getSheetByName(bracketsPage) // Nom de la page sur google sheet à remplir

  var rows = []

  for (i = 0; i < dataSet.length; i++) {
    const data = dataSet[i];
    rows.push([data.setIdentifier, data.phase, data.player1Prefix, data.player1GamerTag, data.player2Prefix, data.player2GamerTag, data.score]);
  }

  clearPage(sheet)
  const dataRange = sheet.getRange(2, 1, rows.length, 7)
  dataRange.setValues(rows);
}

function getStreamQueue() {
  const completeUrl = baseUrl + "streamQueue?eventId=" + getCellValue(streamQueueEventInputCell) + "&streamName=" + getCellValue(streamQueueStreamNameInputCell)
  const dataSet = makeApiCall(completeUrl)
  const sheet = SpreadsheetApp.getActive().getSheetByName(streamQueuePage) // Nom de la page sur google sheet à remplir

  var rows = []

  for (i = 0; i < dataSet.length; i++) {
    const data = dataSet[i];
    rows.push([data.identifier, data.phase, data.pool, data.player1, data.player2]);
  }

  clearPage(sheet)
  const dataRange = sheet.getRange(2, 1, rows.length, 5)
  dataRange.setValues(rows);
}
