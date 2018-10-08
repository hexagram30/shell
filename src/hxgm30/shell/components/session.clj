(ns hxgm30.shell.components.session
  (:require
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.db.plugin.component :as db-component]
    [hxgm30.db.plugin.redis.api.db :as db]
    [hxgm30.shell.components.config :as config]
    [taoensso.timbre :as log])
  (:import
    [java.util UUID]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-db
  [system]
  (get-in system [:session :db]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Session Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn read-data
  "This reader offers 2 arities: one for a known user with a known user id,
  and another (essentially no-op function) for the situations where a user
  is not known (e.g., anonymous users, pre-login). The 1-arity is offered
  for programmatic convenience only."
  ([system]
    {})
  ([system user-id]
    (db/get-session
      (get-db system)
      user-id)))

(defn write-data
  "This reader offers 2 arities: one for a known user with a known user id,
  and another for the situations where a user is not known (e.g., anonymous
  users, pre-login). This will allow for anonymous users to have sessions with
  persistent data that can then be included in their user data, once they
  register."
  ([system data]
    ;; XXX Once the user registration work is complete, use that to create
    ;;     a user id, so that only one function is responsible for
    ;;     id-generation.
    (write-data (str (UUID/randomUUID)) data))
  ([system user-id data]
    (db/update-session
      (get-db system)
      user-id
      data)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord SessionStore
  [db])

(defn start
  [this]
  (let [conn (db-component/db-conn this)
        session-db (db/create-session-db conn)]
    (log/info "Starting session component ...")
    (log/debug "Using connection:" conn)
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

