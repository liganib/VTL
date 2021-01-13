package it.bancaditalia.oss.vtl.eclipse.parts;

import static java.util.Collections.emptyList;
import static org.eclipse.e4.ui.workbench.modeling.EModelService.IN_MAIN_MENU;
import static org.eclipse.jface.text.ITextOperationTarget.REDO;
import static org.eclipse.jface.text.ITextOperationTarget.UNDO;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import it.bancaditalia.oss.vtl.eclipse.impl.grammar.VTLAntlrRuleScanner;
import it.bancaditalia.oss.vtl.eclipse.impl.markers.VTLAnnotationAccess;
import it.bancaditalia.oss.vtl.eclipse.impl.markers.VTLAnnotationPainter;

@SuppressWarnings("restriction")
public class EditorPart
{
	public static final String EDITOR_ELEMENT_ID = "vtl-eclipse-app.editor.template";
	public static final String CLOSE_MENU_ID = "vtl-eclipse-app.menu.file.close";
	private static final Document TEMPLATE_DOC = new Document(System.lineSeparator().repeat(150));

	@Inject
	private MPart part;

	@Inject
	private EModelService modelService;

	@Inject
	private MApplication application;

	@Inject
	private EPartService partService;

	@Inject
	private ECommandService commandService;
	
	private AnnotationModel annotationModel;

	private SourceViewer editor;
	private File fileName;
	private VTLAnnotationPainter painter;
	private VTLAntlrRuleScanner ruleScanner;

	@PostConstruct
	public final void createComposite(final Composite parent)
	{
		fileName = (File) part.getTransientData().get("fileName");
		if (fileName != null)
			part.setLabel(fileName.getName());
		final CompositeRuler ruler = new CompositeRuler(5);
		final LineNumberRulerColumn lineNumberRuler = new LineNumberRulerColumn();
		ruler.addDecorator(0, lineNumberRuler);
		editor = new SourceViewer(parent, ruler, SWT.MULTI | SWT.SEARCH);
		
		annotationModel = new AnnotationModel();
		ruleScanner = new VTLAntlrRuleScanner(annotationModel);
		editor.configure(new EditorConfigurator());

		painter = new VTLAnnotationPainter(editor, new VTLAnnotationAccess());
		editor.addPainter(painter);
		editor.addTextPresentationListener(painter);
		annotationModel.addAnnotationModelListener(painter);
		
		MHandledMenuItem closeMenu = modelService.findElements(application, CLOSE_MENU_ID, MHandledMenuItem.class, emptyList(), IN_MAIN_MENU).get(0);
		partService.addPartListener(new PartListenerAdapter(commandService, closeMenu));
		Display.getDefault().asyncExec(this::lazyInit);
	}

	private void lazyInit()
	{
		// remove old document
		annotationModel.removeAllAnnotations();
		IDocument oldDoc = editor.getDocument();
		if (oldDoc != null && annotationModel != null)
			annotationModel.disconnect(oldDoc);
		
		final Document document = load();
		editor.setDocument(document, annotationModel);
		annotationModel.connect(document);
		document.addDocumentListener(ruleScanner);
		editor.addTextListener(event -> part.setDirty(true));
		editor.setUndoManager(new TextViewerUndoManager(500));
		editor.getUndoManager().connect(editor);
	}

	@Persist
	public void save()
	{
		if (fileName == null)
		{
			String selected = getSaveFile();
			if (selected != null)
				fileName = new File(selected);
		}

		if (fileName != null)
		{
			// TODO: save
			part.setDirty(false);
		}
	}

	private String getSaveFile()
	{
		FileDialog fileDialog = new FileDialog(editor.getControl().getShell(), SWT.SAVE);
		fileDialog.setText("Save as...");
		fileDialog.setFilterExtensions(new String[] { "*.vtl", "*.*" });
		fileDialog.setFilterNames(new String[] { "VTL Scripts (*.vtl)", "All files (*.*)" });
		return fileDialog.open();
	}

	private Document load()
	{
		Document newDocument;
		if (fileName != null)
			try (FileReader reader = new FileReader(fileName))
			{
				StringWriter writer = new StringWriter();
				reader.transferTo(writer);
				newDocument = new Document(writer.toString());
			} 
			catch (IOException e)
			{
				e.printStackTrace();
				newDocument = new Document();
			}
		else
			newDocument = new Document();
		
		return newDocument;
	}

	public void redo()
	{
		Display.getDefault().syncExec(() -> editor.doOperation(REDO));
	}

	public void undo()
	{
		Display.getDefault().syncExec(() -> editor.doOperation(UNDO));
	}
}