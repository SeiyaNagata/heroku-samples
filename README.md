Heroku Samples
==============

Herokuトレーニングのサンプルです。

### 実行方法

```
$ mvn package
$ run
```

### 環境変数

|変数名|説明|例|
|:--|:--|:--|
|DATABASE_URL|接続するDatabase。<br>ローカルではH2のIn Memory Databaseが使用できます。|jdbc:h2:mem:test;DB_CLOSE_DELAY=-1|
