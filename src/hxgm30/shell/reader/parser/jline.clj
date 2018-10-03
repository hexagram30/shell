(ns hxgm30.shell.reader.parser.jline
  (:require
    [hxgm30.shell.reader.parser.base :as base])
  (:import
    [org.jline.reader.Parser$ParseContext]))


(gen-class
    :name #^{org.jline.reader.Parser true}
          hxgm30.shell.reader.parser.JlineParser
    :state state
    :init init
    :constructors {[Object] []})

(defn -parse
  ([^String line ^Integer cursor]
    )
  ([^String line ^Integer cursor ^Parser$ParseContext parse-context]
    ))

(defn create
  [parser-type]
  )


;org.jline.reader.CompletingParsedLine

