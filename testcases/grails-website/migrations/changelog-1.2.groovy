databaseChangeLog = {

    changeSet(author: "pledbrook (generated)", id: "1327422140130-1") {
        createTable(tableName: "wiki_image") {
            column(autoIncrement: "true", name: "id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "wiki_imagePK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "varchar(255)") {
                constraints(nullable: "false")
            }
        }

        createIndex(indexName: "name_idx", tableName: "wiki_image") {
            column(name: "name")
        }
    }

    changeSet(author: "pledbrook (generated)", id: "1327422140130-2") {
        createTable(tableName: "wiki_image_bi_image") {
            column(name: "wiki_image_bi_image_id", type: "bigint")

            column(name: "image_id", type: "bigint")

            column(name: "bi_image_idx", type: "varchar(255)")
        }
    }

    changeSet(author: "pledbrook (generated)", id: "1330418914429-1") {
        createTable(tableName: "plugin_release") {
            column(autoIncrement: "true", name: "id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "plugin_releasPK")
            }

            column(name: "download_url", type: "varchar(255)") {
                constraints(nullable: "false")
            }

            column(name: "plugin_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "release_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "release_version", type: "varchar(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "pledbrook (generated)", id: "1330711514540-1") {
        createTable(tableName: "user_permissions") {
            column(name: "user_id", type: "bigint")

            column(name: "permissions_string", type: "varchar(255)")
        }

        createIndex(indexName: "FKE693E6101ADE5676", tableName: "user_permissions") {
            column(name: "user_id")
        }

        createIndex(indexName: "FK919B5AFB2D0CED0B", tableName: "plugin_release") {
            column(name: "plugin_id")
        }
    }
}
