package de.metas.hostkey.api.impl;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2015 metas GmbH
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


import java.util.UUID;
import org.slf4j.Logger;
import de.metas.logging.LogManager;

import org.adempiere.util.Check;
import de.metas.hostkey.api.IHostKeyBL;
import de.metas.hostkey.spi.IHostKeyStorage;

public class HostKeyBL implements IHostKeyBL
{
	private static final transient Logger logger = LogManager.getLogger(HostKeyBL.class);

	private IHostKeyStorage hostKeyStorage;

	@Override
	public void setHostKeyStorage(final IHostKeyStorage hostKeyStorage)
	{
		this.hostKeyStorage = hostKeyStorage;
	}

	@Override
	public String getHostKey()
	{
		Check.assumeNotNull(hostKeyStorage, "hostKeyStorage is set");
		return getCreateHostKey(this.hostKeyStorage);
	}

	@Override
	public String getCreateHostKey(final IHostKeyStorage hostKeyStorage)
	{
		Check.assumeNotNull(hostKeyStorage, "hostKeyStorage not null");

		String hostkey = hostKeyStorage.getHostKey();
		if (Check.isEmpty(hostkey, true)  
			|| "1841f0fe-c913-4857-98f1-dfba6a759f65".equals(hostkey) // dirty workaround: this hostKey is blacklisted, ignore it
		)
		{
			// generate hostkey if not found
			hostkey = generateHostKey();
			logger.info("HostKey generated: {}", hostkey);
		}

		// Always set back the cookie, because we want to renew the expire date
		hostKeyStorage.setHostKey(hostkey);

		logger.debug("HostKey found: {}", hostkey);
		return hostkey;
	}

	@Override
	public final String generateHostKey()
	{
		final String hostkey = UUID.randomUUID().toString();
		return hostkey;
	}

}
