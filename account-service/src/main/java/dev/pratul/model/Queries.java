package dev.pratul.model;

public final class Queries {

	private Queries() {
	}

	public static final String GET_ACCOUNTS_BY_USER = "select acc.id as id," + " acc.acc_id as acc_id,"
			+ " acc.acc_name as acc_name," + " acc.status as status from"
			+ " public.account acc left outer join public.user_account_map ua1" + " on acc.id=ua1.acc_id"
			+ " left outer join public.users u" + " on ua1.user_id=u.id"
			+ " left outer join public.user_account_map ua2" + " on acc.id=ua2.acc_id where"
			+ " acc.status=true and ua1.status=true and u.id=?";

	public static final String GET_ACCOUNT_DETAILS = "select acc.id as id," + " acc.acc_id as acc_id,"
			+ " acc.acc_name as acc_name," + " acc.status as status,"
			+ " u.id as user_id, u.first_name as first_name, u.last_name as last_name, u.middle_initial as middle_initial,"
			+ " u.email as email" + " from" + " public.account acc left outer join public.user_account_map ua1"
			+ " on acc.id=ua1.acc_id" + " left outer join public.users u" + " on ua1.user_id=u.id"
			+ " left outer join public.user_account_map ua2" + " on acc.id=ua2.acc_id where" + " acc.id=?";
}
