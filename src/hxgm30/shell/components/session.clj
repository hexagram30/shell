(ns hxgm30.shell.components.session
  (:require
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.db.plugin.component :as db-component]
    [hxgm30.db.plugin.redis.api.db :as db]
    [hxgm30.shell.components.config :as config]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TBD

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Session Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn read-data
  [system user-id]
  (db/get (db-component/db-conn system)
          user-id))

(defn write-data
  [system user-id data]
  (db/update (db-component/db-conn system)
             user-id
             data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord SessionStore
  [db])

(defn start
  [this]
  (let [session-db (db/create-session-db (db-component/db-conn this))]
    (log/info "Starting session component ...")
    (log/debug "Using connection:" (db-component/db-conn this))
    (log/debug "Started session component.")
    (assoc this :db session-db)))

(defn stop
  [this]
  (log/info "Stopping session component ...")
  (log/debug "Stopped session component.")
  (assoc this :db nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend SessionStore
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->SessionStore {:db nil}))

