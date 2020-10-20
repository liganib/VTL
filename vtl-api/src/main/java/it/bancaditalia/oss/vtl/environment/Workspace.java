/**
 * Copyright © 2020 Banca D'Italia
 *
 * Licensed under the EUPL, Version 1.2 (the "License");
 * You may not use this work except in compliance with the
 * License.
 * You may obtain a copy of the License at:
 *
 * https://joinup.ec.europa.eu/sites/default/files/custom-page/attachment/2020-03/EUPL-1.2%20EN.txt
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the License is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */
package it.bancaditalia.oss.vtl.environment;

import java.util.List;
import java.util.Optional;

import it.bancaditalia.oss.vtl.config.VTLGeneralProperties;
import it.bancaditalia.oss.vtl.engine.Statement;

/**
 * An environment that is capable of storing and retrieving VTL rules.
 * 
 * A class implementing this interface must be listed in the 
 * {@link VTLGeneralProperties#ENVIRONMENT_IMPLEMENTATION} property for the VTL engine to work. 
 * 
 * @author Valentino Pinna
 *
 */
public interface Workspace extends Environment
{
	/**
	 * Returns an {@link Optional} reference to a {@link Statement} with the specified name in this environment.
	 * 
	 * @param name The name of requested rule.
	 * @return An Optional with a reference to the requested object or {@link Optional#empty()} if the object is not found in this environment.
	 */
	public Optional<? extends Statement> getRule(String name);

	/**
	 * Add a new rule to this workspace.
	 * 
	 * @param statement The rule to add to this workspace.
	 */
	public void addRule(Statement statement);
	
	/**
	 * @return A list of all the rules defined in this workspace.
	 */
	public List<Statement> getRules();
}
