(ns hxgm30.shell.components.core
  (:require
    [com.stuartsierra.component :as component]
    [hxgm30.shell.components.config :as config]
    [hxgm30.shell.components.logging :as logging]
    [hxgm30.shell.components.nrepl :as nrepl]
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

; (def mock-terminal
;   {:terminal (component/using
;               (terminal/create-component)
;               [:config :logging])})

(defn common
  [cfg-data]
  (merge (cfg cfg-data)
         log))

(defn shell
  [cfg-data]
  (merge (common cfg-data)
         ;mock-terminal
         ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Initializations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-bare-bones
  []
  (-> (config/build-config)
      common
      component/map->SystemMap))

(defn initialize
  []
  (-> (config/build-config)
      shell
      component/map->SystemMap))

(def init-lookup
  {:basic #'initialize-bare-bones
   :default #'initialize})

(defn init
  ([]
    (init :default))
  ([mode]
    ((mode init-lookup))))
