# recipelib #

## Build & Run ##

Install sbt launcher via brew or apt. 

```sh
$ cd recipelib
$ sbt
> container:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

To reload on changes:

```
~;container:start
```

For some reason, container:reload didn't seem to work:

http://stackoverflow.com/questions/8545783/auto-reloading-files-in-scala-lift-sbt-11

...
