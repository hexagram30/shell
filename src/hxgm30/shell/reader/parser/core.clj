(ns hxgm30.shell.reader.parser.core
  (:require
    [hxgm30.shell.reader.parser.base :as base]))

(defn create
  [parser-type]
  (case parser-type
    :base (base/create)))
