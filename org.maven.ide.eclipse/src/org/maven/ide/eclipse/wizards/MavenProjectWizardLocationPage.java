/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.wizards;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;

import org.maven.ide.eclipse.core.Messages;
import org.maven.ide.eclipse.project.ProjectImportConfiguration;


/**
 * Wizard page used to specify project location and working set.
 */
public class MavenProjectWizardLocationPage extends AbstractMavenWizardPage {

  Button useDefaultWorkspaceLocationButton;
  Label locationLabel;
  Combo locationCombo;

  boolean initialized = false;
  private WorkingSetGroup workingSetGroup;

  private IPath location;

  /**
   * Creates Maven project location page.
   * 
   * @param title location page title text
   * @param description location page description text
   */
  public MavenProjectWizardLocationPage(ProjectImportConfiguration configuration, String title, String description) {
    super("MavenProjectWizardLocationPage", configuration);
    setTitle(title);
    setDescription(description);
    setPageComplete(false);
    
  }

  /**
   * Creates Maven project location page.
   */
  public MavenProjectWizardLocationPage(ProjectImportConfiguration configuration, boolean showSimpleProject) {
    super("MavenProjectWizardLocationPage", configuration);
    setTitle(Messages.getString("wizard.project.page.project.title"));
    setDescription(Messages.getString("wizard.project.page.project.description"));
    setPageComplete(false);
  }
  
  /**
   * {@inheritDoc} This wizard page contains a component to query the project name and a
   * <code>MavenLocationComponent</code> which allows to specify whether the project should be created in the
   * workspace or at some given external location.
   */
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NULL);
    container.setLayout(new GridLayout(3, false));

    createAdditionalControls(container);
    
//    // project name
//    GridData gridData = new GridData();
//    Label label = new Label(container, SWT.NULL);
//    label.setLayoutData(gridData);
//    label.setText(Messages.getString("wizard.project.page.project.projectName"));
//    projectNameText = new Combo(container, SWT.BORDER | SWT.SINGLE);
//    projectNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//    projectNameText.addModifyListener(modifyingListener);
//    addFieldWithHistory("projectName", projectNameText);

    // gridData.verticalIndent = 5;
//    locationComponent = new MavenLocationComponent(container, SWT.NONE);
//    locationComponent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 3, 1));
//    locationComponent.setModifyingListener(modifyingListener);
//    addFieldWithHistory("location", locationComponent.getLocationCombo());

    useDefaultWorkspaceLocationButton = new Button(container, SWT.CHECK);
    GridData useDefaultWorkspaceLocationButtonData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
    useDefaultWorkspaceLocationButton.setLayoutData(useDefaultWorkspaceLocationButtonData);
    useDefaultWorkspaceLocationButton.setText("Use default &Workspace location");
    useDefaultWorkspaceLocationButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        boolean inWorkspace = isInWorkspace();
        locationLabel.setEnabled(!inWorkspace);
        locationCombo.setEnabled(!inWorkspace);
      }
    });
    useDefaultWorkspaceLocationButton.setSelection(true);
    
    locationLabel = new Label(container, SWT.NONE);
    GridData locationLabelData = new GridData();
    locationLabelData.horizontalIndent = 10;
    locationLabel.setLayoutData(locationLabelData);
    locationLabel.setText("&Location:");
    locationLabel.setEnabled(false);
    
    locationCombo = new Combo(container, SWT.NONE);
    GridData locationComboData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    locationCombo.setLayoutData(locationComboData);
    locationCombo.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        validate();
      }
    });
    locationCombo.setEnabled(false);
    addFieldWithHistory("location", locationCombo);
    
    Button locationBrowseButton = new Button(container, SWT.NONE);
    GridData locationBrowseButtonData = new GridData(SWT.FILL, SWT.CENTER, false, false);
    locationBrowseButton.setLayoutData(locationBrowseButtonData);
    locationBrowseButton.setText("Brows&e...");
    locationBrowseButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setText("Select Location");
        
        String path = locationCombo.getText();
        if(path.length()==0) {
          path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
        }
        dialog.setFilterPath(path);
        
        String selectedDir = dialog.open();
        if(selectedDir != null) {
          locationCombo.setText(selectedDir);
          useDefaultWorkspaceLocationButton.setSelection(false);
          validate();
        }
      }
    });

    this.workingSetGroup = new WorkingSetGroup(container, getImportConfiguration(), getShell());

    if(location==null || Platform.getLocation().equals(location)) {
//      useDefaultWorkspaceLocationButton.setSelection(true);
    } else {
//      useDefaultWorkspaceLocationButton.setSelection(false);
//      locationLabel.setEnabled(true);
//      locationCombo.setEnabled(true);
      locationCombo.setText(location.toOSString());
    }
    
    createAdvancedSettings(container, new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
    
    setControl(container);
  }

  /**
   * Create additional controls
   */
  protected void createAdditionalControls(Composite container) {
  }

  public void dispose() {
    super.dispose();
    workingSetGroup.dispose();
  }
  
  /**
   * Returns whether the user has chosen to create the project in the workspace or at an external location.
   * 
   * @return <code>true</code> if the project is to be created in the workspace, <code>false</code> if it should be
   *         created at an external location.
   */
  public boolean isInWorkspace() {
    return useDefaultWorkspaceLocationButton.getSelection();
  }

  /**
   * Returns the path of the location where the project is to be created. According to the user input, the path either
   * points to the workspace or to a valid user specified location on the filesystem.
   * 
   * @return The path of the location where to create the project. Is never <code>null</code>.
   */
  public IPath getLocationPath() {
    if(isInWorkspace()) {
      return ResourcesPlugin.getWorkspace().getRoot().getLocation();
    }
    return Path.fromOSString(locationCombo.getText().trim());
  }
  
  public void setLocationPath(IPath location) {
    this.location = location;
  }
  
  /** {@inheritDoc} */
  public void setVisible(boolean visible) {
    super.setVisible(visible);

    if(visible) {
      initialized = true;
      validate();
//      projectNameText.setFocus();
    }
  }

  /**
   * Validates the contents of this wizard page.
   * <p>
   * Feedback about the validation is given to the user by displaying error messages or informative messages on the
   * wizard page. Depending on the provided user input, the wizard page is marked as being complete or not.
   * <p>
   * If some error or missing input is detected in the user input, an error message or informative message,
   * respectively, is displayed to the user. If the user input is complete and correct, the wizard page is marked as
   * begin complete to allow the wizard to proceed. To that end, the following conditions must be met:
   * <ul>
   * <li>The user must have provided a project name.</li>
   * <li>The project name must be a valid project resource identifier.</li>
   * <li>A project with the same name must not exist.</li>
   * <li>A valid project location path must have been specified.</li>
   * </ul>
   * </p>
   * 
   * @see org.eclipse.core.resources.IWorkspace#validateName(java.lang.String, int)
   * @see org.eclipse.core.resources.IWorkspace#validateProjectLocation(org.eclipse.core.resources.IProject,
   *      org.eclipse.core.runtime.IPath)
   * @see org.eclipse.jface.dialogs.DialogPage#setMessage(java.lang.String)
   * @see org.eclipse.jface.wizard.WizardPage#setErrorMessage(java.lang.String)
   * @see org.eclipse.jface.wizard.WizardPage#setPageComplete(boolean)
   */
  protected void validate() {
    if (!initialized) {
      return;
    }

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

//    final String name = getProjectName();
//
//    // check whether the project name field is empty
//    if(name.trim().length() == 0) {
//      setErrorMessage(null);
//      setMessage(Messages.getString("wizard.project.page.project.validator.projectName"));
//      setPageComplete(false);
//      return;
//    }
//
//    // check whether the project name is valid
//    final IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
//    if(!nameStatus.isOK()) {
//      setErrorMessage(nameStatus.getMessage());
//      setPageComplete(false);
//      return;
//    }
//
//    // check whether project already exists
//    final IProject handle = getProjectHandle();
//    if(handle.exists()) {
//      setErrorMessage(Messages.getString("wizard.project.page.project.validator.projectExists"));
//      setPageComplete(false);
//      return;
//    }

    IPath projectPath = getLocationPath();
    String location = projectPath.toOSString();

    // check whether location is empty
    if(location.length() == 0) {
      setErrorMessage(null);
      setMessage(Messages.getString("wizard.project.page.project.validator.projectLocation"));
      setPageComplete(false);
      return;
    }

    // check whether the location is a syntactically correct path
    if(!Path.ROOT.isValidPath(location)) {
      setErrorMessage(Messages.getString("wizard.project.page.project.validator.invalidLocation"));
      setPageComplete(false);
      return;
    }

    // If we do not place the contents in the workspace validate the location.
    if(!isInWorkspace()) {
      String projectName = getImportConfiguration().getProjectName(((MavenProjectWizard)getWizard()).getModel());
      if(projectName.length()>0){
        final IStatus locationStatus = workspace.validateProjectLocation(workspace.getRoot().getProject(projectName), projectPath);
        if(!locationStatus.isOK()) {
          setErrorMessage(locationStatus.getMessage());
          setPageComplete(false);
          return;
        }
      }
    }

    setPageComplete(true);
    setErrorMessage(null);
    setMessage(null);
  }

}