/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.m2e.tests.ui.editing;

import org.eclipse.jface.text.IDocument;
import static org.eclipse.m2e.core.ui.internal.editing.PomEdits.*;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;




public class PomEditsTest extends TestCase {

	private IDOMModel tempModel;


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
	  tempModel = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(
          "org.eclipse.m2e.core.pomFile");
	}


	public void testRemoveIfNoChildElement() {
		tempModel.getStructuredDocument().setText(StructuredModelManager.getModelManager(), 
				"<project>" +
				"<build>" +
				"<pluginManagement>" +
				"<plugins></plugins" + 
				"</pluginManagement>" + 
				"</build>" + 
				"</project>");
		Document doc = tempModel.getDocument();
		Element plugins = findChild(findChild(findChild(doc.getDocumentElement(), BUILD), PLUGIN_MANAGEMENT), PLUGINS);
		assertNotNull(plugins);
		removeIfNoChildElement(plugins);
		assertNull(findChild(doc.getDocumentElement(), BUILD));
		
		tempModel.getStructuredDocument().setText(StructuredModelManager.getModelManager(), 
				"<project>" +
				"<build>" +
				"<pluginManagement>" +
				"<plugins></plugins" + 
				"</pluginManagement>" + 
				"<STOP_ELEMENT/>" + 
				"</build>" + 
				"</project>");
		doc = tempModel.getDocument();
		plugins = findChild(findChild(findChild(doc.getDocumentElement(), BUILD), PLUGIN_MANAGEMENT), PLUGINS);
		assertNotNull(plugins);
		removeIfNoChildElement(plugins);
		Element build = findChild(doc.getDocumentElement(), BUILD);
		assertNotNull(build);
		assertNull(findChild(build, PLUGIN_MANAGEMENT));
		
	}
	
	
	public void testMatchers() {
		tempModel.getStructuredDocument().setText(StructuredModelManager.getModelManager(), 
				"<dependencies>" +
				"<dependency><groupId>AAA</groupId><artifactId>BBB</artifactId><tag1/></dependency>" +
				"<dependency><groupId>AAAB</groupId><artifactId>BBB</artifactId><tag2/></dependency>" +
				"<dependency><groupId>AAA</groupId><artifactId>BBBB</artifactId><tag3/></dependency>" +
				"<dependency><artifactId>BBB</artifactId><tag4/></dependency>" +
				"</dependencies>");
		Document doc = tempModel.getDocument();
		Element el = findChild(doc.getDocumentElement(), DEPENDENCY, childEquals(ARTIFACT_ID, "BBBB"));
		assertNotNull(findChild(el, "tag3"));
		
		el = findChild(doc.getDocumentElement(), DEPENDENCY, childEquals(ARTIFACT_ID, "BBB"));
		assertNotNull(findChild(el, "tag1"));
		
		el = findChild(doc.getDocumentElement(), DEPENDENCY, childEquals(GROUP_ID, "AAAB"), childEquals(ARTIFACT_ID, "BBB"));
		assertNotNull(findChild(el, "tag2"));
		
		el = findChild(doc.getDocumentElement(), DEPENDENCY, childMissingOrEqual(GROUP_ID, "CCC"), childEquals(ARTIFACT_ID, "BBB"));
		assertNotNull(findChild(el, "tag4"));
		
		el = findChild(doc.getDocumentElement(), DEPENDENCY, childEquals(GROUP_ID, "AAA"), childMissingOrEqual(ARTIFACT_ID, "BBBB"));
		assertNotNull(findChild(el, "tag3"));
	}
}