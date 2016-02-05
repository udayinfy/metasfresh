package org.adempiere.acct.api;

import org.adempiere.exceptions.AdempiereException;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
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

/**
 * Exception thrown when {@link IGLDistributionBL#validate(org.compiere.model.I_GL_Distribution)} fails.
 * 
 * @author metas-dev <dev@metas-fresh.com>
 *
 */
public class GLDistributionNotValidException extends AdempiereException
{
	private static final long serialVersionUID = -1707655052772443217L;

	public GLDistributionNotValidException(final String message)
	{
		super(message);
	}
}