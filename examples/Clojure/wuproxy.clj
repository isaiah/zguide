(ns wuproxy
  (:refer-clojure :exclude [send])
  (:require [zhelpers :as mq]))

;;
;; Weather proxy device
;;
;; Isaiah Peng <issaria@gmail.com>
;;

(defn -main []
  (let [ctx (mq/context 1)
        frontend (mq/socket ctx mq/sub)
        backend (mq/socket ctx mq/pub)]
    (mq/connect frontend "tcp://*:5556")
    (mq/bind backend "tcp://10.18.102.1:8100")
    ;; Subscribe on everything
    (mq/subscribe frontend "")
    (while (not (.isInterrupted (Thread/currentThread)))
      (while true
        (let [message (mq/recv-str frontend)
              more (.hasReceiveMore frontend)]
          (if more
            (mq/send backend message mq/sndmore)
            (mq/send backend message 0)))))))
