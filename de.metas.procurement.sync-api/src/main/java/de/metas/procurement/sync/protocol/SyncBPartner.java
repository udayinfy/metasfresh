package de.metas.procurement.sync.protocol;

import java.util.ArrayList;
import java.util.List;

/*
 * #%L
 * de.metas.fresh.procurement.webui
 * %%
 * Copyright (C) 2016 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

public class SyncBPartner extends AbstractSyncModel
{
	private String name;
	private List<SyncUser> users = new ArrayList<>();
	private List<SyncContract> contracts = new ArrayList<>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<SyncUser> getUsers()
	{
		return users;
	}

	public void setUsers(List<SyncUser> users)
	{
		this.users = users;
	}

	public List<SyncContract> getContracts()
	{
		return contracts;
	}

	public void setContracts(List<SyncContract> contracts)
	{
		this.contracts = contracts;
	}

}