(ns hxgm30.shell.dev
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as repl]
    [clojusc.twig :as logger]
    [hxgm30.shell.grammar :as grammar]
    [hxgm30.shell.parser :as parser]))

(logger/set-level! ['hxgm30] :info)

(def refresh #'repl/refresh)
