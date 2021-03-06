/*
 * ====================================================================
 * JAFFA - Java Application Framework For All
 *
 * Copyright (C) 2002 JAFFA Development Group
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Redistribution and use of this software and associated documentation ("Software"),
 * with or without modification, are permitted provided that the following conditions are met:
 * 1.	Redistributions of source code must retain copyright statements and notices.
 *         Redistributions must also contain a copy of this document.
 * 2.	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 * 3.	The name "JAFFA" must not be used to endorse or promote products derived from
 * 	this Software without prior written permission. For written permission,
 * 	please contact mail to: jaffagroup@yahoo.com.
 * 4.	Products derived from this Software may not be called "JAFFA" nor may "JAFFA"
 * 	appear in their names without prior written permission.
 * 5.	Due credit should be given to the JAFFA Project (http://jaffa.sourceforge.net).
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package org.jaffa.loader.components;

import org.jaffa.loader.IManager;
import org.jaffa.loader.IRepository;
import org.jaffa.loader.MapRepository;
import org.jaffa.presentation.portlet.component.ComponentDefinition;
import org.jaffa.presentation.portlet.component.ComponentDefinitionException;
import org.jaffa.presentation.portlet.component.componentdomain.Component;
import org.jaffa.presentation.portlet.component.componentdomain.Components;
import org.jaffa.util.JAXBHelper;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

/**
 * A class that manages various kinds of component object specifications, as
 * read in from a configuration file.
 * Created by kcassell on 8/7/2017.
 */
public class ComponentManager implements IManager {

    /** The name of the configuration file which this class handles. */
    private static final String DEFAULT_COMPONENTS_FILE = "components.xml";

    /** The location of the schema for the configuration file. */
    private static final String COMPONENT_XSD =
            "org/jaffa/presentation/portlet/component/componentdomain/" +
                    "component-definitions_1_1.xsd";

    /** The name of the configuration file which this class handles. */
    private String configurationFile = DEFAULT_COMPONENTS_FILE;

    /** The ComponentDefinition repository.  The key is the component name of
     * the value in the ComponentDefinition object. */
    private IRepository<String, ComponentDefinition> componentRepository =
            new MapRepository<>();

    /**
     * Unmarshall the contents of the configuration to create and register
     * ComponentDefinition, QueueInfo, TopicInfo, and/or MessageFilter objects.
     * @param resource the object that contains the xml config file.
     * @param context key with which config file to be registered.
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException for file opening or reading errors, or when an
     * attempt to create a ComponentDefinition throws a
     * ComponentDefinitionException
     */
    @Override
    public void registerXML(Resource resource, String context)
            throws JAXBException, SAXException, IOException {

        Components components = JAXBHelper.unmarshalConfigFile(Components.class,
                resource, COMPONENT_XSD);

        List<Component> componentList = components.getComponent();

        if (componentList != null) {
            for (final Component component : componentList) {
                ComponentDefinition definition;
                try {
                    definition = new ComponentDefinition(component);
                } catch (ComponentDefinitionException e) {
                    // wrap the thrown exception in an IOException to conform
                    // to the interface
                    throw new IOException(e.getMessage(), e);
                }
                registerComponentDefinition(definition.getComponentName(),
                        definition, context);
            }   // for
        }
    }

    /**
     * retrieves the ComponentDefinition from the repository
     * @param name key used for the repository
     * @param contextOrderParam Order of the contexts used for retrieval
     * @return ComponentDefinition
     */
    public ComponentDefinition getComponentDefinition(String name,
                                      List<String> contextOrderParam) {
        return componentRepository.query(name, contextOrderParam);
    }


    ////////////////////////////////////////////////////////////////////////
    //////////////////////////// Registration //////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * Register ComponentDefinition in the repository
     * @param key the key associated with the value in the repository
     * @param context with which repository to be associated with
     * @param value the object to store
     */
    public void registerComponentDefinition(
            String key, ComponentDefinition value, String context) {
        componentRepository.register(key, value, context);
    }

    /**
     * Unregister a ComponentDefinition object from the repository
     * @param key the key for the value being removed from the repository
     * @param context with which repository to be associated with
     */
    public void unregisterComponentDefinition(String key, String context) {
        componentRepository.unregister(key, context);
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////////////////// Simple Accessors ////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    @Override
    public String getXmlFileName() {
        return configurationFile;
    }

    public IRepository<String, ComponentDefinition> getComponentRepository() {
        return componentRepository;
    }

    public void setComponentRepository(
            IRepository<String, ComponentDefinition> repository) {
        this.componentRepository = repository;
    }

}
