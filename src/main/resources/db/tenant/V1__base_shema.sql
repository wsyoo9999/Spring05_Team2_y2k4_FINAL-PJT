CREATE TABLE accounts (
                          ac_id	BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                          ac_name	varchar(50)	NOT NULL,
                          ac_manager varchar(50),
                          ac_loc	varchar(250),
                          ac_phone	varchar(100),
                          ac_email    VARCHAR(100),
                          CONSTRAINT uq_accounts_email UNIQUE (ac_email)
);


CREATE TABLE stock (
                       stock_id     BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                       stock_name   VARCHAR(100) NOT NULL,
                       qty          INT UNSIGNED NOT NULL DEFAULT 0,
                       unit_price   DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                       location     VARCHAR(30),
                       type         tinyint,
);


CREATE TABLE human_resource (
                                emp_id       BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                emp_name     VARCHAR(50) NOT NULL,
                                supervisor   BIGINT UNSIGNED,
                                dept_name    VARCHAR(50),
                                position     VARCHAR(30),
                                hire_date    DATE,
                                bank_name    VARCHAR(50),
                                bank_account VARCHAR(50),
                                status       tinyint,
                                phone_number VARCHAR(20),
                                CONSTRAINT fk_hr_supervisor FOREIGN KEY (supervisor) REFERENCES human_resource(emp_id)
);

CREATE TABLE work_order (
                            work_order_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                            stock_id      BIGINT UNSIGNED NOT NULL,
                            emp_id        BIGINT UNSIGNED NOT NULL,
                            start_date    DATE,
                            due_date      DATE,
                            target_qty    INT UNSIGNED,
                            good_qty      INT UNSIGNED DEFAULT 0,
                            defect_qty    INT UNSIGNED DEFAULT 0,
                            order_status  tinyint,
                            request_date    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_wo_stock FOREIGN KEY (stock_id) REFERENCES stock(stock_id),
                            CONSTRAINT fk_wo_emp   FOREIGN KEY (emp_id)   REFERENCES human_resource(emp_id)
);


CREATE TABLE lot (
                     lot_id       BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                     work_order_id BIGINT UNSIGNED NOT NULL,
                     stock_id     BIGINT UNSIGNED NOT NULL,
                     lot_qty      INT UNSIGNED NOT NULL,
                     lot_date     DATE,
                     CONSTRAINT fk_lot_wo   FOREIGN KEY (work_order_id) REFERENCES work_order(work_order_id),
                     CONSTRAINT fk_lot_stock FOREIGN KEY (stock_id)     REFERENCES stock(stock_id)
);

CREATE TABLE inbound (
                         inbound_id  BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                         inbound_date DATETIME NOT NULL,
                         stock_id        BIGINT UNSIGNED NOT NULL,
                         inbound_qty             INT UNSIGNED NOT NULL,
                         unit_price      DECIMAL(15,2) DEFAULT 0.00,
                         ac_id       BIGINT UNSIGNED,
                         emp_id      BIGINT UNSIGNED NOT NULL,
                         remark      varchar(200),
                         approval tinyint NOT NULL
                             CONSTRAINT fk_inb_ac  FOREIGN KEY (ac_id)  REFERENCES accounts(ac_id)
                         CONSTRAINT fk_inb_emp FOREIGN KEY (emp_id) REFERENCES human_resource(emp_id),
);

CREATE TABLE outbound (
                          outbound_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                          outbound_date DATETIME NOT NULL,
                          stock_id         BIGINT UNSIGNED NOT NULL,
                          outbound_qty              INT UNSIGNED NOT NULL,
                          ac_id       BIGINT UNSIGNED,
                          emp_id      BIGINT UNSIGNED NOT NULL,
                          remark varchar(200),
                          approval tinyint NOT NULL default 0
                              CONSTRAINT fk_out_ac  FOREIGN KEY (ac_id)  REFERENCES accounts(ac_id)
                          CONSTRAINT fk_out_emp FOREIGN KEY (emp_id) REFERENCES human_resource(emp_id),
);

CREATE TABLE salary (
                        salary_id  BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                        emp_id     BIGINT UNSIGNED NOT NULL,
                        pay_date   DATE NOT NULL,
                        pay        DECIMAL(15,2) NOT NULL,
                        deduction  DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                        total_pay  DECIMAL(15,2),
                        CONSTRAINT fk_sal_emp FOREIGN KEY (emp_id) REFERENCES human_resource(emp_id)
);


CREATE TABLE attendance (
                            att_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                            emp_id        BIGINT UNSIGNED NOT NULL,
                            att_date      DATETIME NOT NULL,
                            att_status    tinyint NOT NULL,
                            clock_out     DATETIME,
                            CONSTRAINT fk_att_emp FOREIGN KEY (emp_id) REFERENCES human_resource(emp_id),
                            CONSTRAINT uq_attendance UNIQUE (emp_id, att_date)
);


CREATE TABLE documents (
                           doc_id     BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                           req_id     BIGINT UNSIGNED NOT NULL,
                           req_date   DATETIME DEFAULT CURRENT_TIMESTAMP,
                           title      VARCHAR(200),
                           content    TEXT,
                           appr_id    BIGINT UNSIGNED,
                           appr_date  DATETIME,
                           status     tinyint,
                           comments   Text,
                           CONSTRAINT fk_doc_req  FOREIGN KEY (req_id)  REFERENCES human_resource(emp_id),
                           CONSTRAINT fk_doc_appr FOREIGN KEY (appr_id) REFERENCES human_resource(emp_id)
);


CREATE TABLE purchase (
                          purchase_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                          emp_id      BIGINT UNSIGNED NOT NULL,
                          ac_id       BIGINT UNSIGNED NOT NULL,
                          order_date  DATE,
                          del_date    DATE,
                          total_price DECIMAL(15,2) DEFAULT 0.00,
                          status     tinyint,
                          CONSTRAINT fk_pur_emp FOREIGN KEY (emp_id) REFERENCES human_resource(emp_id),
                          CONSTRAINT fk_pur_ac  FOREIGN KEY (ac_id) REFERENCES accounts(ac_id)
);

CREATE TABLE purchase_details (
                                  pd_id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                  purchase_id    BIGINT UNSIGNED NOT NULL,
                                  stock_id       BIGINT UNSIGNED NOT NULL,
                                  purchase_qty            INT UNSIGNED NOT NULL,
                                  qty            INT UNSIGNED,
                                  price_per_unit DECIMAL(15,2) NOT NULL,
                                  total_price    DECIMAL(15,2),
                                  CONSTRAINT fk_pl_purchase FOREIGN KEY (purchase_id) REFERENCES purchase(purchase_id) ON DELETE CASCADE,
                                  CONSTRAINT fk_pl_stock    FOREIGN KEY (stock_id)    REFERENCES stock(stock_id)
);


CREATE TABLE sale (
                      sale_id     BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                      emp_id      BIGINT UNSIGNED NOT NULL,
                      ac_id       BIGINT UNSIGNED NOT NULL,
                      order_date  DATE,
                      due_date    DATE,
                      total_price DECIMAL(15,2) DEFAULT 0.00,
                      status      tinyint,
                      CONSTRAINT fk_sale_emp FOREIGN KEY (emp_id) REFERENCES human_resource(emp_id),
                      CONSTRAINT fk_sale_ac  FOREIGN KEY (ac_id) REFERENCES accounts(ac_id)
);

CREATE TABLE sale_details (
                              sd_id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                              sale_id        BIGINT UNSIGNED NOT NULL,
                              stock_id       BIGINT UNSIGNED NOT NULL,
                              qty            INT UNSIGNED NOT NULL,
                              price_per_unit DECIMAL(15,2) NOT NULL,
                              total_price    DECIMAL(15,2),
                              CONSTRAINT fk_sd_sale  FOREIGN KEY (sale_id)  REFERENCES sale(sale_id) ON DELETE CASCADE,
                              CONSTRAINT fk_sd_stock FOREIGN KEY (stock_id) REFERENCES stock(stock_id)
);

CREATE TABLE bom (
                     bom_id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                     parent_stock_id   BIGINT UNSIGNED NOT NULL,
                     child_stock_id    BIGINT UNSIGNED NOT NULL,
                     required_qty      INT UNSIGNED NOT NULL,
                     CONSTRAINT fk_bom_parent FOREIGN KEY (parent_stock_id) REFERENCES stock(stock_id),
                     CONSTRAINT fk_bom_child  FOREIGN KEY (child_stock_id)  REFERENCES stock(stock_id),
                     CONSTRAINT uq_bom UNIQUE (parent_stock_id, child_stock_id)	-- 자재랑 완재품의 관계를 유일하게
);

CREATE TABLE profit (
                        profit_id     BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                        profit_code   tinyint,
                        profit        DECIMAL(15,2) NOT NULL,
                        profit_date   DATETIME NOT NULL,
                        profit_comment Text
);

CREATE TABLE spend (
                       spend_id      BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                       spend_code    tinyint,
                       spend         DECIMAL(15,2) NOT NULL,
                       spend_date    DATETIME NOT NULL,
                       spend_comment Text
);



CREATE TABLE defect (
                        defect_id   BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                        lot_id      BIGINT UNSIGNED NOT NULL,
                        defect_code tinyint,
                        defect_qty  INT UNSIGNED NOT NULL,
                        defect_date DATE,
                        CONSTRAINT fk_defect_lot  FOREIGN KEY (lot_id)      REFERENCES lot(lot_id)
);
