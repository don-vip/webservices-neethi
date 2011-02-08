/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.neethi.builders;


import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.All;
import org.apache.neethi.Assertion;
import org.apache.neethi.Constants;
import org.apache.neethi.ExactlyOne;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyComponent;

/**
 * 
 */
public class PrimitiveAssertion implements Assertion {
    
    protected QName name;
    protected boolean optional;
    protected boolean ignorable;
    
    public PrimitiveAssertion() {
        this((QName)null);
    }
    
    public PrimitiveAssertion(QName n) {
        this(n, false);
    }
    
    public PrimitiveAssertion(QName n, boolean o) {
        name = n;
        optional = o;
    }
    public PrimitiveAssertion(QName n, boolean o, boolean i) {
        name = n;
        optional = o;
        ignorable = i;
    }

    public String toString() {
        return name.toString();
    }
    public boolean equal(PolicyComponent policyComponent) {
        if (policyComponent.getType() != Constants.TYPE_ASSERTION) {
            return false;
        }
        return getName().equals(((Assertion)policyComponent).getName());
    }

    public short getType() {
        return Constants.TYPE_ASSERTION;
    }

    public QName getName() {
        return name;
    }
    
    public void setName(QName n) {
        name = n;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean o) {
        optional = o;        
    }
    public boolean isIgnorable() {
        return ignorable;
    }

    public void setIgnorable(boolean i) {
        ignorable = i;
    }
    
    public PolicyComponent normalize() {
        if (isOptional()) {
            Policy policy = new Policy();
            ExactlyOne exactlyOne = new ExactlyOne();

            All all = new All();
            all.addPolicyComponent(cloneMandatory());
            exactlyOne.addPolicyComponent(all);
            exactlyOne.addPolicyComponent(new All());
            policy.addPolicyComponent(exactlyOne);

            return policy;
        }

        return cloneMandatory();
    }

    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        String namespace = Constants.findPolicyNamespace(writer);
        writer.writeEmptyElement(name.getNamespaceURI(), name.getLocalPart());
        if (optional) {
            writer.writeAttribute(namespace, Constants.ATTR_OPTIONAL, "true");
        }
        if (ignorable) {
            writer.writeAttribute(namespace, Constants.ATTR_IGNORABLE, "true");
        }
        writer.writeEndElement();
    }
    
    protected Assertion cloneMandatory() {
        return new PrimitiveAssertion(name, false, ignorable);
    }

    public Policy getPolicy() {
        return null;
    }

}