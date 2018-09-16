(ns hxgm30.shell.impl.entry
  (:require
    [hxgm30.shell.reader.parser :as parser])
  (:import
    (java.net InetAddress)
    (java.util Date))
  (:refer-clojure :exclude [empty parse]))

(defrecord EntryShell [
  grammar-type
  prompt
  legal-subshells
  active-subshell
  disconnect-command
  disconnect-handler])

(defn banner
  [this]
  (str "Welcome to "
       (.getHostName (InetAddress/getLocalHost))
       "!\r\n"
       "It is "  (new Date) " now.\r\n"))

(defn parse
  [this request]
  (parser/parse :demo
                (:disconnect-command this)
                request))

(defn render
  [this response]
  (str
   (apply format (concat [(:result-tmpl response)]
                         (:result-args response)))
   ))

(defn handle-request
  [this request]
  (let [response (parse this request)]
    {:response response
     :message (render this response)}))

(def behaviour
  {:banner banner
   :parse parse
   :render render
   :handle-request handle-request})

(defn create-shell
  ([]
    (create-shell {}))
  ([opts]
    (map->EntryShell (merge {:disconnect-command "bye"
                             :grammar-type :demo}
                            opts))))
