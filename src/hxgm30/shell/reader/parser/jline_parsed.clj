(ns hxgm30.shell.reader.parser.jline-parsed
  (:import
    [org.jline.reader.Parser$ParseContext]))

(gen-class
    :name #^{org.jline.reader.CompletingParsedLine true}
          hxgm30.shell.reader.parser.JlineParsedLine
    :state state
    :init init)

(defn -parse
  ([^String line ^Integer cursor]
    )
  ([^String line ^Integer cursor ^Parser$ParseContext parse-context]
    ))

(defn create
  [parser-type]
  )


;

