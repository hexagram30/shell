(ns hxgm30.shell.formatter
  (:require
    [clojure.string :as string]
    [hxgm30.common.util :as util]))

(def text-indent 4)
(def list-indent 12)
(def wrap-column 76)
(def new-line "\r\n")

(defn indent
  ([]
    (indent text-indent))
  ([spaces]
    (repeat spaces \space)))

(defn list-item
  [list-item]
  [new-line (indent list-indent) list-item])

(defn list-items
  ([list-items]
    (list-items "" list-items))
  ([help-text list-items]
    (string/join
      ""
      (flatten [(indent) help-text
               (map list-item list-items)
               new-line]))))

(defn paragraph
  [long-line]
  (str (util/wrap-paragraph long-line wrap-column text-indent)
       new-line))
