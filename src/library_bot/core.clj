(ns library-bot.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [clojure.set :refer [difference]]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.api :as t]
            [clj-http.client :as client]
            [hickory.core :as hc]
            [hickory.select :as hs])
  (:gen-class))

(def old-books (atom {}))

(def token "<BOT-TOKEN>")
(def id    "<CHAT-ID>")
(def jwt   "<JWT-TOKEN>")
(def url   "<SITE-URL>")

(h/defhandler handler

  (h/command-fn "start"
    (fn [{{id :id :as chat} :chat}]
      (println "Bot joined new chat: " chat)
      (t/send-text token id "Welcome to library-bot!")))

  (h/command-fn "help"
    (fn [{{id :id :as chat} :chat}]
      (println "Help was requested in " chat)
      (t/send-text token id "Help is on the way")))

  (h/message-fn
    (fn [{{id :id} :chat :as message}]
      (println "Intercepted message: " message)
      (t/send-text token id "I don't do a whole lot ... yet."))))

(def site-htree (-> (client/get url {:cookies {"token" {:value jwt :path "/"}}})
                    :body
                    hc/parse
                    hc/as-hickory))

(defn get-books-table []
  (-> (hs/select (hs/child
                  (hs/tag :tbody))
                 site-htree)
      first
      :content))

(defn get-books-titles []
  (map #(get-in % [:content 0 :content 0 :content 1]) (get-books-table)))

(defn compare-books-titles []
  (let [new-books (get-books-titles)]
    (when (not= @old-books new-books)
      (do
        (reset! old-books new-books)
        (map #(t/send-text token id (str "New book: " %)) (difference new-books @old-books))
        (str "New books: " (difference new-books @old-books))))))

(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))

  (println "Starting the library-bot")

  (<!! (do
         (println "Getting old books")
         (p/start token handler)

         (reset! old-books (get-books-titles))

         (while true
           (println "Checking for new books")
           (println (compare-books-titles))
           (Thread/sleep (* 15 60 1000))))))
