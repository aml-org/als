---
site: https://anypoint.mulesoft.com/apiplatform/popular/admin/#/dashboard/apis/7711/versions/7844/portal/pages/6426/preview
apiNotebookVersion: 1.1.66
title: XKCD API Notebook
---

```javascript
load('https://github.com/chaijs/chai/releases/download/1.9.0/chai.js')
```

See http://chaijs.com/guide/styles/ for assertion styles

```javascript
assert = chai.assert
```

```javascript
// Read about the Xksd at https://anypoint.mulesoft.com/apiplatform/popular/admin/#/dashboard/apis/7711/versions/7844/contracts
API.createClient('client', '#REF_TAG_DEFENITION');
```

If you want to fetch current comic and metadata automatically,
you can use the JSON interface.

```javascript
currentComic = client("/info.0.json").get()
```

```javascript
assert.equal(currentComic.status,200,"Error")
comicId = currentComic.body.num
```

If you want to fetch comics and metadata  by comic id automatically,
you can use the JSON interface.

```javascript
comicResponse = client.comicId(comicId)["info.0.json"].get()
```

```javascript
assert.equal(comicResponse.status,200,"Error")
```