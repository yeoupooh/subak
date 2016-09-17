using System;
using Gtk;

public partial class MainWindow : Gtk.Window
{
	public MainWindow() : base(Gtk.WindowType.Toplevel)
	{
		Build();
	}

	protected void OnDeleteEvent(object sender, DeleteEventArgs a)
	{
		Application.Quit();
		a.RetVal = true;
	}

	protected void OnButton1Clicked(object sender, EventArgs e)
	{
		Console.WriteLine(entry1.Text);
		TreeView child = new TreeView();
		TreeStore model = new TreeStore(typeof(string), typeof(string));
		for (int i = 0; i < 5; i++)
		{
			TreeIter iter = model.AppendValues("Demo " + i, "Data " + i);
		}
		child.Model = model;
		child.AppendColumn("Demo", new CellRendererText(), "text", 0);
		child.AppendColumn("Data", new CellRendererText(), "text", 1);
		notebook2.AppendPage(child, new Label(entry1.Text));
		child.Show();
	}
}
