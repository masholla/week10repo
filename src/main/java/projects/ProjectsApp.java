package projects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp 
{
	//private variables
	//@formatter:off
	private List<String> operations = List.of(
			"1) Add a project" + "\n" +
			"2) List projects" + "\n" +
			"3) Select a project" + "\n" //+
			//"4) Update project details"
			);
	//@formatter:on
	private Scanner scanner = new Scanner(System.in);
	ProjectService projectService = new ProjectService();
	private Project currentProject;
	private int printOp = 1;
	
	public static void main(String[] args) 
	{
		//new instance of projects app calling method processUserSelections
		new ProjectsApp().processUserSelections();
		
	}

	private void processUserSelections() 
	{
		boolean done = false;
		
		while (done == false)
		{
			try
			{
				int select = getUserSelection();
				
				switch (select)
				{
				case -1:
					done = exitMenu();
					break;
					
				case 1:
					createProject();
					break;
					
				case 2:
					viewProjects();
					break;
					
				case 3:
					selectProject();
					break;
					
				case 4:
					updateProjectDetails();
					break;
					
				default: System.out.println("\n" + select + " is not a valid selection. Try again.");
				}
			}
			catch(Exception e)
			{
				System.out.println("\nError: " + e + " Try again.\n");
			}
		}
	}

	private void updateProjectDetails() 
	{
		if(currentProject == null)
		{
			System.out.println("\nPlease select a project.");
			return;
		}else
		{
			System.out.println("\nWhat would you like to do with the following project: " + currentProject.getProjectName());
		}
			
	}

	private void selectProject() 
	{
		//Shows projects to choose from
		viewProjects();
		
		//creates integer to hold selection value from user to pick which project should be selected
		Integer projectIdNumber = getIntInput("Enter project ID to select a project: ");
		
		//nulls currentProject
		currentProject = null;
		
		// sets cp to resulting project from project id called through project service
		//throws error if incorrect projectIdNumber is sent IN PROJECTSERVICE
		currentProject = projectService.grabSpecificProject(projectIdNumber);
		
		
	}

	private void viewProjects() 
	{
		//makes an instance of a project list to replicate from called classes/methods
		List<Project> projects = projectService.grabAllThoseProjects();
		
		System.out.println("\nProjects:");
		
		for(Project project : projects)
		{
			System.out.println(" " + project.getProjectId() + ": " + project.getProjectName());
		}
		System.out.println();
		
		//testing further
		//for(Project project : projects)
		//{
		//	System.out.println(project);
		//}
	}

	private void createProject() 
	{
		//gets all user data
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		//creates project to be populated
		Project project = new Project();
		
		//populates project with taken info
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setEstimatedHours(estimatedHours);
		project.setNotes(notes);
		project.setProjectName(projectName);
		
		//creates new project and makes it equal to what returns after sending it to projectService/projectDao
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}

	private BigDecimal getDecimalInput(String prompt) 
	{
		String input = getStringInput(prompt);
		double inputValue = Double.parseDouble(input);
		
		if(Objects.isNull(input))
		{
			return null;
		}
		
		try
		{
			return BigDecimal.valueOf(inputValue).setScale(2);
		}
		catch(NumberFormatException e)
		{
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	private boolean exitMenu() 
	{
		System.out.println("Exiting the menu.");
		return true;
	}

	private int getUserSelection() 
	{
		//gives user options
		printOperations(printOp);
		
		// calls to get value of int with a printable prompt
		Integer input = getIntInput("Enter a menu selection");
		
		// checks if it is null then returns
		return Objects.isNull(input) ? -1 : input;
	}

	private Integer getIntInput(String prompt) 
	{
		//calls getStringInput for the user input
		String input = getStringInput(prompt);
		
		//checks if it is null
		if(Objects.isNull(input))
		{
			return null;
		}
		//tests to see if input was an int, if it wasnt -- throws
		try
		{
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e)
		{
			throw new DbException(input + " is not a valid number.");
		}
	}

	private String getStringInput(String prompt) 
	{
		//accepts user input
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		//either returns a string or null if null
		return input.isBlank() ? null : input.trim();
	}

	private void printOperations(int printOp) 
	{
		if(printOp == 1)
		{
			System.out.println("These are the available operations. Press the Enter key to quit.");
			for(String op : operations)System.out.println(op);
		}else
			if(printOp == 0)
			{
				
			}else
				System.out.println("Failed to print Operations");
		
		//prints object unless object is null, which then states there is no project.
		if(Objects.isNull(currentProject))
		{
			System.out.println("\nThere is currently no project selected.");
		}
		else
		{
			System.out.println("\nYou are working with project: " + currentProject);
		}
	}
}
