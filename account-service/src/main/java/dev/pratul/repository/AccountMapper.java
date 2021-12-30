package dev.pratul.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dev.pratul.dto.AccountDto;
import dev.pratul.dto.UserDto;

public class AccountMapper implements RowMapper<AccountDto> {

	@Override
	public AccountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		AccountDto accountDto = new AccountDto();
		accountDto.setId(rs.getLong("id"));
		accountDto.setAccountId(rs.getString("acc_id"));
		accountDto.setAccountName(rs.getString("acc_name"));
		accountDto.setStatus(rs.getBoolean("status"));
		if (rs.getMetaData().getColumnCount() > 4) {
			UserDto userDto = new UserDto();
			userDto.setId(rs.getLong("user_id"));
			userDto.setFirstName(rs.getString("first_name"));
			userDto.setLastName(rs.getString("last_name"));
			userDto.setMiddleInitial(rs.getString("middle_initial"));
			userDto.setEmail(rs.getString("email"));
			accountDto.getUsers().add(userDto);
		}
		return accountDto;
	}

}
