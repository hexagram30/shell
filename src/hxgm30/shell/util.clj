(ns hxgm30.shell.util
  (:require
    [clj-fuzzy.phonetics :as phonetics]
    [clojure.string :as string]))

(def metaphone2-set
  (comp set
        #(map (comp keyword string/lower-case) %)
        phonetics/double-metaphone
        name))

(defn metaphone2-sets
  "Given a collection, get the double metaphone for each element, converting
  to a set of unique lower-cased keyword."
  [coll]
  (map metaphone2-set coll))

(defn metaphone2-map
  [element]
  (->> element
       (metaphone2-set)
       (map (fn [x] (vector x element)))
       (into {})))

(defn metaphone2-maps
  [coll]
  (map metaphone2-map coll))

(defn metaphone2-lookup
  [coll]
  (apply merge (metaphone2-maps coll)))

(defn get-metaphone2
  [lookup key-name]
  (->> key-name
       metaphone2-set
       (map #(% lookup))
       (remove nil?)
       set))
