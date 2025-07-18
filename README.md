# 📉📈 InflatioFix 📈📉

InflatioFix is a turn-based simulation designed to model the complex interactions among three key market participants:  

🛒 Sellers, who offer a limited quantity of products and set prices based on production costs, inflation, and profit margins. Their objective is to maximize profit by balancing these factors.  
🛍️ Buyers, who seek to satisfy their needs for essential and luxury goods while minimizing expenses. Buyers monitor product prices and inflation rates, adjusting their purchasing decisions accordingly. Their willingness to buy decreases as prices rise, regardless of whether the increase is due to inflation or sellers’ margins. Buyers aim to fulfill their needs within their budget constraints.  
🏦 The Central Bank, which monitors price growth and market turnover, dynamically adjusting the inflation rate to stabilize tax revenues. The Bank strives to maintain stable tax income calculated as the product of inflation and market turnover, despite changing economic conditions.  
📉 The system simulates a dynamic economic environment where these participants interact strategically, responding to market fluctuations with the goal of reaching economic equilibrium.  

The project includes designing tests to ensure the Central Bank’s tax revenues remain stable and resilient to disturbances within the market.

### Market disturbances visualization 🖥️
<img width="982" height="556" alt="InflatioFix" src="https://github.com/user-attachments/assets/9514d2fe-b3e1-4a27-99d6-84139f4a9339" />  


# 💻 Technologies Used   

InflatioFix is built using the following technologies:  

📍 Java – the main language used for implementing the game logic, models, and design patterns  
📍 SOLID Principles – applied to design a clear, testable, and scalable architecture  
📍 Design Patterns Used:  
  • Observer Pattern for passive monitoring of system changes (e.g., inflation levels, price changes)  
  • Visitor Pattern to update the state of agents in the simulation (e.g., changing purchasing or pricing strategies)  
  • 📊 Data flow diagrams can be found in the file [schemes.pdf](https://github.com/hsialitskaya/InflatioFix/blob/main/schemes.pdf)   
📍 Python – used for data analysis and generating charts illustrating:  
  • inflation changes over time,  
  • market turnovers,  
  • purchasing and selling decisions  
📍 GSON (Google JSON) – utilized for serialization and deserialization of data between turns, as well as for passing data to the visualization system in Python  
📍 JUnit – for unit testing simulation functionalities, including:  
  • stability of the Central Bank’s tax revenues,  
  • buyers’ reactions to price changes,  
  • evolution of sellers’ strategies    

# 🏁 Getting Started  

To get started with the InflatioFixp, follow these steps:  

1️⃣ Clone the Repository      

Download the repository to your local machine by running the following command in your terminal:    
```bash
git clone https://github.com/hsialitskaya/InflatioFix.git InflatioFix
```  

2️⃣ Set Up the Java     

This project uses GSON for JSON serialization and deserialization. Make sure your Java environment is set up and dependencies are installed (e.g., via Maven or Gradle).  


3️⃣ Run the Simulation  

Navigate to the project directory and run the main Java class located in src. This will generate the simulation data file simulation.json:  
```bash
cd src
java -cp . Main
```

4️⃣ Run Tests

To verify the simulation’s correctness, run the unit tests located in the test/ directory, e.g., using JUnit.  


5️⃣ Run Python Data Analysis  

Navigate to the Python analysis folder:  
```bash
cd python/
```

Install dependencies via:  
```bash
pip install -r requirements.txt  
```  

Run the analysis script:  
```bash  
python plot.py  
```

This will automatically generate visual plots based on the `simulation.json` file, including:  

- 📈 **Inflation trends over time**    
- 💰 **Market turnover**    
- 🛒 **Buying and selling behaviors of agents**  


## License  
InflatioFix is licensed under the MIT License. See [LICENSE](https://github.com/hsialitskaya/InflatioFix/blob/main/LICENSE) for more information.      


Enjoy navigating the dynamic world of market forces and have fun mastering the art of balancing inflation and strategy! 🎉  
