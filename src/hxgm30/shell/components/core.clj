(ns hxgm30.shell.components.core
  (:require
    [com.stuartsierra.component :as component]
    [hxgm30.db.plugin.backend :as backend]
    [hxgm30.shell.components.config :as config]
    [hxgm30.shell.components.logging :as logging]
    [hxgm30.shell.components.nrepl :as nrepl]
    [hxgm30.shell.components.registry :as registry]
    [hxgm30.shell.components.session :as session]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common Configuration Components   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cfg
  [cfg-data]
  {:config (config/create-component cfg-data)})

(defn db
  [cfg-data]
  (let [backend (get-in cfg-data [:backend :plugin])]
    {:backend (component/using
               (backend/create-component backend)
               [:config :logging])}))

(def log
  {:logging (component/using
             (logging/create-component)
             [:config])})

(def nrepl
  {:nrepl (component/using
           (nrepl/create-component)
           [:config :logging])})

(def sess
  {:session (component/using
             (session/create-component)
             [:config :logging])})

;;; Additional components for systems that want to supress logging (e.g.,
;;; systems created for testing).

(defn db-without-logging
  [cfg-data]
  (let [backend (get-in cfg-data [:backend :plugin])]
    {:backend (component/using
               (backend/create-component backend)
               [:config])}))

(def sess-without-logging
  {:session (component/using
             (session/create-component)
             [:config])})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Initializations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-config-only
  []
  (component/map->SystemMap (config/build-config)))

(defn initialize-bare-bones
  []
  (-> (config/build-config)
      cfg
      (merge log)
      component/map->SystemMap))

(defn initialize
  []
  (let [cfg-data (config/build-config)]
  (-> cfg-data
      cfg
      (merge log
             ;;nrepl
             (db cfg-data)
             sess)
      component/map->SystemMap)))

(defn initialize-without-logging
  []
  (let [cfg-data (config/build-config)]
  (-> cfg-data
      cfg
      (merge ;;nrepl
             (db-without-logging cfg-data)
             sess-without-logging)
      component/map->SystemMap)))

(def init-lookup
  {:basic #'initialize-bare-bones
   :main #'initialize
   :testing-config-only #'initialize-config-only
   :testing #'initialize-without-logging})

(defn init
  ([]
    (init :main))
  ([mode]
    ((mode init-lookup))))
