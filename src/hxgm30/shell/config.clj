(ns hxgm30.shell.config
  (:require
   [hxgm30.common.file :as common]))

(def config-file "hexagram30-config/shell.edn")

(defn data
  ([]
    (data config-file))
  ([filename]
    (common/read-edn-resource filename)))
