(ns hxgm30.shell.repl
  (:require
    [clj-fuzzy.phonetics :as phonetics]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.string :as string]
    [clojure.tools.namespace.repl :as repl]
    [clojusc.system-manager.core :refer :all]
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.shell.components.config :as config]
    [hxgm30.shell.components.core]
    [hxgm30.shell.components.session :as session]
    [hxgm30.shell.core :as shell]
    [hxgm30.shell.evaluator :as evaluator]
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.reader.grammar.core :as grammar]
    [hxgm30.shell.reader.grammar.entry :as entry-grammar]
    [hxgm30.shell.reader.parser.core :as parser]
    [taoensso.timbre :as log]
    [trifl.java :refer [show-methods]])
  (:import
    (java.net URI)
    (java.nio.file Paths)
    (org.jline.reader LineReaderBuilder)
    (org.jline.reader.impl.completer StringsCompleter)
    (org.jline.terminal TerminalBuilder)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Initial Setup & Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def setup-options {
  :init 'hxgm30.shell.components.core/init
  :after-refresh 'hxgm30.shell.repl/init-and-startup
  :throw-errors false})

(defn init
  "This is used to set the options and any other global data.

  This is defined in a function for re-use. For instance, when a REPL is
  reloaded, the options will be lost and need to be re-applied."
  []
  (logger/set-level! '[hxgm30] :debug)
  (setup-manager setup-options))

(defn init-and-startup
  "This is used as the 'after-refresh' function by the REPL tools library.
  Not only do the options (and other global operations) need to be re-applied,
  the system also needs to be started up, once these options have be set up."
  []
  (init)
  (startup))

;; It is not always desired that a system be started up upon REPL loading.
;; Thus, we set the options and perform any global operations with init,
;; and let the user determine when then want to bring up (a potentially
;; computationally intensive) system.
(init)

(defn banner
  []
  (println (slurp (io/resource "text/banner.txt")))
  :ok)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Other   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn start-mock-terminal
  []
  (reset)
  (ns-unalias (find-ns 'hxgm30.terminal.util.networkless) 'shell)
  (load "/hxgm30/terminal/util/networkless")
  (let [start-server (ns-resolve 'hxgm30.terminal.util.networkless 'start-server)]
    (start-server)))


; (def in-stream (new java.io.InputStream))
; (def out-stream (new java.io.OutputStream))
; (def terminal
;   (doto (TerminalBuilder/builder)
;         (.name "Hexagram30 JLine Terminal")
;         (.system false)
;         (.streams in-stream out-stream)
;         (.jansi true)
;         (.build)))

; (def completer (new StringsCompleter "foo" "bar" "baz")

; (def reader
;   (doto (LineReaderBuilder/builder)
;         (.appName "Hexagram30 Entry Shell")
;         (.terminal terminal)
;         (.completer completer)
;         (.parser parser)
;         ;(.history history)
;         ))
