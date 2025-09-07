import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import os


mean_omitted = []
mean_read = []
thresholds = []
ratios = []

foldersRN = []
foldersBA = []

min_value = -1.0
max_value = 1.0
step = 0.1

base_path3 = "RESULTS0,00/500000/randomNetwork/"
base_path4 = "RESULTS0,20/500000/randomNetwork/"
base_path5 = "RESULTS0,40/500000/randomNetwork/"
base_path = "RESULTS0,80/500000/randomNetwork/"

current = min_value
while current <= max_value + 1e-8:
    formatted = f"{current:.2f}".replace('.', ',')
    foldersRN.append(base_path + formatted + "/")
    foldersBA.append(base_path.replace("randomNetwork", "BA") + formatted + "/")
    current = round(current + step, 10)  # unikamy błędów binarnej reprezentacji floató

for f in foldersBA:
    plots_dir = os.path.join(f, "plots")
    os.makedirs(plots_dir, exist_ok=True)

    def folder(i):
        return  f"{f}{i}_steps.txt"
    df = pd.read_csv(folder(1), sep="\t")
    df1 = pd.read_csv(folder(2), sep="\t")
    df = pd.concat([df, df1], ignore_index=True)

    topologia = "graf przypadkowy" if "randomNetwork" in f else "sieć BA"
    #threshold = f.split('/')[3]  # Pobieramy próg z nazwy folderu
    similarity = f.split('/')[0]  # Pobieramy podobieństwo z nazwy folderu
    similarity = similarity.split('RESULTS')[1]    
    
    df.loc[df['messages_interested_amount'] > 0, 'messages_read'] = 1
    df.loc[df['messages_interested_amount'] == 0, 'messages_read'] = 0
    

    #minus one for values > 0 in column messages_interested_amount
    df.loc[df['messages_interested_amount'] > 0, 'messages_interested_amount'] -= 1

    mean_omitted.append(df['messages_interested_amount'].mean())
    mean_read.append(df['messages_read'].mean())
    thresholds.append(f.split("/")[-2])
    ratios.append(df['messages_read'].mean() / df['messages_interested_amount'].mean() if df['messages_interested_amount'].mean() != 0 else np.nan)
plt.figure(figsize=(10, 8))
plt.errorbar(thresholds, mean_omitted, yerr=df['messages_interested_amount'].std(), fmt='o', color='b', ecolor='b', capsize=5,capthick=2,alpha=0.8, label='liczba wiadomości ominiętych')
plt.errorbar(thresholds, mean_read, yerr=df['messages_read'].std(), fmt='o', color='r', ecolor='r',capsize=5, capthick=2,alpha=0.8, label='liczba wiadomości przeczytanych')
plt.plot(thresholds, ratios, linestyle='--', color='g', label='Stosunek (Przeczytane/Ominięte)', marker='x', markersize=5)

plt.legend(loc='upper right', fontsize=14)
plt.grid(True)
plt.xticks(rotation=45)
#make less x ticks
plt.xticks(np.arange(0, len(thresholds), step=2))
plt.xlabel('τ',fontsize=16)
plt.ylabel('Średnia ilość wiadomości',fontsize=16)
plt.tick_params(axis='both', labelsize=16)
#plt.title('Messages read and omitted in different thresholds',fontsize=18) 
plt.title(topologia, fontsize=18)
plt.savefig(f + "plots/" + topologia + "_Ising=" + similarity + "_thresholds.png")
plt.show()




mean_omitted = []
mean_read = []
thresholds = []
ratios = []
for f in foldersRN:
    plots_dir = os.path.join(f, "plots")
    os.makedirs(plots_dir, exist_ok=True)

    def folder(i):
        return  f"{f}{i}_steps.txt"
    df = pd.read_csv(folder(1), sep="\t")
    df1 = pd.read_csv(folder(2), sep="\t")
    df = pd.concat([df, df1], ignore_index=True)
        
    topologia = "graf przypadkowy" if "randomNetwork" in f else "sieć BA"
    #threshold = f.split('/')[3]  # Pobieramy próg z nazwy folderu
    similarity = f.split('/')[0]  # Pobieramy podobieństwo z nazwy folderu
    similarity = similarity.split('RESULTS')[1]  

    df.loc[df['messages_interested_amount'] > 0, 'messages_read'] = 1
    df.loc[df['messages_interested_amount'] == 0, 'messages_read'] = 0
    

    #minus one for values > 0 in column messages_interested_amount
    df.loc[df['messages_interested_amount'] > 0, 'messages_interested_amount'] -= 1

    mean_omitted.append(df['messages_interested_amount'].mean())
    mean_read.append(df['messages_read'].mean())
    thresholds.append(f.split("/")[-2])
    ratios.append(df['messages_read'].mean() / df['messages_interested_amount'].mean() if df['messages_interested_amount'].mean() != 0 else np.nan)

plt.figure(figsize=(10, 8))
plt.errorbar(thresholds, mean_omitted, yerr=df['messages_interested_amount'].std(), fmt='o', color='b', ecolor='b', capsize=5,capthick=2,alpha=0.8, label='liczba wiadomości ominiętych')
plt.errorbar(thresholds, mean_read, yerr=df['messages_read'].std(), fmt='o', color='r', ecolor='r',capsize=5, capthick=2,alpha=0.8, label='liczba wiadomości przeczytanych')
plt.plot(thresholds, ratios, linestyle='--', color='g', label='Stosunek (Przeczytane/Ominięte)', marker='x', markersize=5)
plt.legend(loc='upper right', fontsize=14)
plt.grid(True)
plt.xticks(rotation=45)
plt.xticks(np.arange(0, len(thresholds), step=2))
plt.xlabel('τ',fontsize=16)
plt.ylabel('Średnia ilość wiadomości',fontsize=16)
plt.tick_params(axis='both', labelsize=16)
plt.title(topologia, fontsize=18)
plt.savefig(f + "plots/" + topologia + "_Ising=" + similarity + "_thresholds.png")
plt.show()


