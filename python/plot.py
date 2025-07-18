import json
import matplotlib.pyplot as plt
from statistics import mean

# Wczytaj dane
with open('../simulation.json') as f:
    data = json.load(f)

turns = [d['turn'] for d in data]
inflation = [d['inflation'] for d in data]
bank_turnover = [d['bankTurnover'] for d in data]

def avg_sellers(data, key, seller_type):
    result = []
    for d in data:
        values = [s[key] for s in d['sellers'] if s['type'] == seller_type]
        if values:
            result.append(mean(values))
        else:
            result.append(None)
    return result

price_essential = avg_sellers(data, 'price', 'essential')
price_luxury = avg_sellers(data, 'price', 'luxury')

margin_essential = avg_sellers(data, 'margin', 'essential')
margin_luxury = avg_sellers(data, 'margin', 'luxury')

stock_essential = avg_sellers(data, 'stock', 'essential')
stock_luxury = avg_sellers(data, 'stock', 'luxury')

def avg_buyers(data, key):
    result = []
    for d in data:
        values = [b[key] for b in d['buyers']]
        if values:
            result.append(mean(values))
        else:
            result.append(None)
    return result

budget_buyers = avg_buyers(data, 'budget')
essentials_bought = avg_buyers(data, 'essentials')
luxuries_bought = avg_buyers(data, 'luxuries')

def plot_chart(x, y, label, title, ylabel, color):
    plt.figure(figsize=(12,6))
    plt.plot(x, y, label=label, color=color)
    plt.title(title)
    plt.xlabel("Tura")
    plt.ylabel(ylabel)
    plt.grid(True)
    plt.legend()
    plt.show()

plot_chart(turns, inflation, 'Inflacja', 'Zmiany inflacji w czasie', 'Inflacja', 'red')
plot_chart(turns, bank_turnover, 'Obrót banku', 'Obrót banku w czasie', 'Obrót banku', 'green')

plot_chart(turns, price_essential, 'Cena produktów podstawowych', 'Ceny produktów podstawowych', 'Cena', 'blue')
plot_chart(turns, price_luxury, 'Cena produktów luksusowych', 'Ceny produktów luksusowych', 'Cena', 'purple')

plot_chart(turns, margin_essential, 'Marża produktów podstawowych', 'Marża produktów podstawowych', 'Marża', 'cyan')
plot_chart(turns, margin_luxury, 'Marża produktów luksusowych', 'Marża produktów luksusowych', 'Marża', 'magenta')

plot_chart(turns, stock_essential, 'Zapasy produktów podstawowych', 'Zapasy produktów podstawowych', 'Ilość w magazynie', 'orange')
plot_chart(turns, stock_luxury, 'Zapasy produktów luksusowych', 'Zapasy produktów luksusowych', 'Ilość w magazynie', 'brown')

plot_chart(turns, budget_buyers, 'Średni budżet kupujących', 'Średni budżet kupujących', 'Budżet', 'black')
plot_chart(turns, essentials_bought, 'Średnia liczba produktów podstawowych kupionych', 'Zakupy produktów podstawowych', 'Ilość', 'darkblue')
plot_chart(turns, luxuries_bought, 'Średnia liczba produktów luksusowych kupionych', 'Zakupy produktów luksusowych', 'Ilość', 'darkred')
