(ns hxgm30.shell.components.core
  (:require
    [com.stuartsierra.component :as component]
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Initializations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-bare-bones
  []
  (-> (config/build-config)
      cfg
      (merge log)
      component/map->SystemMap))

(defn initialize
  []
  (-> (config/build-config)
      cfg
      (merge log
             ;;nrepl
             sess)
      component/map->SystemMap))

(def init-lookup
  {:basic #'initialize-bare-bones
   :main #'initialize})

(defn init
  ([]
    (init :main))
  ([mode]
    ((mode init-lookup))))
