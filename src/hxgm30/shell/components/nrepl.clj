(ns hxgm30.shell.components.nrepl
  (:require
    [clojure.tools.nrepl.server :as nrepl]
    [com.stuartsierra.component :as component]
    [hxgm30.shell.components.config :as config]
    [taoensso.timbre :as log]))

;;; XXX This is the nREPL server ...
;;;     we'd actually need an nREPL client, too ...

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   nREPL Server Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TBD

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord REPL [server])

(defn start
  [this]
  (log/info "Starting nREPL component ...")
  (let [port (config/nrepl-port this)
        server (nrepl/start-server :port port)]
    (log/debugf "nREPL server is listening on port %s" port)
    (log/debug "Started nREPL component.")
    (assoc this :server server)))

(defn stop
  [this]
  (log/info "Stopping nREPL component ...")
  (if-let [server (:server this)]
    (nrepl/stop-server server))
  (log/debug "Stopped nREPL component.")
  (assoc this :server nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend REPL
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->REPL {}))
