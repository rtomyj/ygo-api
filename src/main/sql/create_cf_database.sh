DB_NAME=$1

cf mysql ${DB_NAME} -f < "cards.sql"

cf mysql ${DB_NAME} -f < "cards_normal.sql"
cf mysql ${DB_NAME} -f < "cards_effect.sql"
cf mysql ${DB_NAME} -f < "cards_ritual.sql"
cf mysql ${DB_NAME} -f < "cards_fusion.sql"
cf mysql ${DB_NAME} -f < "cards_synchro.sql"
cf mysql ${DB_NAME} -f < "cards_xyz.sql"
cf mysql ${DB_NAME} -f < "cards_normal_pendulum.sql"
cf mysql ${DB_NAME} -f < "cards_effect_pendulum.sql"
cf mysql ${DB_NAME} -f < "cards_link.sql"
cf mysql ${DB_NAME} -f < "cards_spell.sql"
cf mysql ${DB_NAME} -f < "cards_trap.sql"

cf mysql ${DB_NAME} -f < "2018-12-03_ban_list.sql"
cf mysql ${DB_NAME} -f < "2019-01-28_ban_list.sql"
cf mysql ${DB_NAME} -f < "2019-04-29_ban_list.sql"
cf mysql ${DB_NAME} -f < "2019-07-15_ban_list.sql"
cf mysql ${DB_NAME} -f < "2019-10-14_ban_list.sql"