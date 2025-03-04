(ns server.models.labels
  (:require
   [server.db.sql.queries :as db])
  (:gen-class))

(defn get-labels
  []
  (db/query-database ["SELECT
                         id,
                         name,
                         TO_CHAR(created_at, 'FMMM/FMDD/YYYY, FMHH12:MI:SS AM') AS created_at
                       FROM labels
                       ORDER BY id ASC"]))