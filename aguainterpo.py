import tkinter as tk
from tkinter import messagebox
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from tkinter import messagebox
from scipy.interpolate import interp1d


# Leer datos desde Excel
#archivo = "datos.xlsx"

archivo = "water.csv"

df = pd.read_csv(archivo)

#df=pd.read_excel(archivo)

# Extraer propiedades del aire (cada columna)
x = df['x'].values
y1 = df['y1'].values
y2 = df['y2'].values
y3 = df['y3'].values
y4 = df['y4'].values
y5 = df['y5'].values
y6 = df['y6'].values
y7 = df['y7'].values
y8 = df['y8'].values
y9 = df['y9'].values

def calcular():
    try:
        x= float(entry3.get())
        x_consulta = x

        y_consulta1 = f_interp1(x_consulta)
        y_consulta2 = f_interp2(x_consulta)
        y_consulta3 = f_interp3(x_consulta)
        y_consulta4 = f_interp4(x_consulta)
        y_consulta5 = f_interp5(x_consulta)
        y_consulta6 = f_interp6(x_consulta)
        y_consulta7 = f_interp7(x_consulta)
        y_consulta8 = f_interp8(x_consulta)
        y_consulta9 = f_interp9(x_consulta)



        # Actualiza el texto en la misma etiqueta
        #resultado_label1.config(text=str(y_consulta1))
        #print(format(y_consulta1, ".4f")) 
        resultado_label1.config(text=format(y_consulta1, ".4f"))
        resultado_label2.config(text=format(y_consulta2, ".4f"))
        resultado_label3.config(text=format(y_consulta3, ".4f"))
        resultado_label4.config(text=format(y_consulta4, ".4f"))
        resultado_label5.config(text=format(y_consulta5, ".4f"))
        resultado_label6.config(text=format(y_consulta6, ".4f"))
        resultado_label7.config(text=format(y_consulta7, ".4f"))
        resultado_label8.config(text=format(y_consulta8, ".4f"))
        resultado_label9.config(text=format(y_consulta9, ".4f"))
        
        #messagebox.showinfo("Resultado", f"Interpolación en x={x_consulta}: y={y_consulta1}")
        
    except ValueError:
        messagebox.showerror("Error", "Por favor ingresa números válidos")


def limpiar_entry():
    """Limpia el contenido del campo de texto."""
    entry3.delete(0, tk.END)  # Borra desde el primer carácter hasta el final
    resultado_label1.config(text="")
    resultado_label2.config(text="")
    resultado_label3.config(text="")
    resultado_label4.config(text="")
    resultado_label5.config(text="")
    resultado_label6.config(text="")
    resultado_label7.config(text="")
    resultado_label8.config(text="")
    resultado_label9.config(text="")




# Configuración de la ventana
root = tk.Tk()
root.title("Propiedades del agua")
root.configure(background="orange")
#root.minsize(600, 600)
#root.maxsize(800, 800)
root.geometry("450x450+150+150")
root.resizable(False, False)  # (ancho, alto)

#tk.Label(root, text="Ingrese temperatura del aire °C:").pack()

label=tk.Label(root, text="Ingrese temperatura del agua °C:")
label.place(x=80, y=30)
#tk.Label(root, text="Temperatura:")
entry3 = tk.Entry(root)

#entry3.grid(row=0, column=0, padx=10, pady=10)


#entry3.pack()
entry3.place(x=270, y=30)  # Coordenadas absolutas



# Crear etiquetas de texto (Resultados)
#etiqueta1 = tk.Label(
    #root,
    #text="Coeficiente de expansión volumétrica K-1",
    #font=("Arial", 10),
    #fg="blue"
#)
#etiqueta1.pack(pady=1)  # Empaquetar con espacio vertical

# Valor presión saturación
etiqueta1 = tk.Label(root, text="Presión de saturación KPa", bg="yellow", font=("Arial", 10))
etiqueta1.place(x=50, y=110)  # Coordenadas absolutas

# Valor b
etiqueta2 = tk.Label(root, text="Coeficiente de expansión volumétrica K-1", bg="yellow", font=("Arial", 10))
etiqueta2.place(x=50, y=140)  # Coordenadas absolutas

# Valor r
etiqueta3 = tk.Label(root, text="Densidad kg/m3", bg="yellow", font=("Arial", 10))
etiqueta3.place(x=50, y=170)  # Coordenadas absolutas

# Valor Cp
etiqueta4 = tk.Label(root, text="Calor específico kJ/kg K", bg="yellow", font=("Arial", 10))
etiqueta4.place(x=50, y=200)  # Coordenadas absolutas

# Valor k
etiqueta5 = tk.Label(root, text="Conductividad termal W/m K", bg="yellow", font=("Arial", 10))
etiqueta5.place(x=50, y=230)  # Coordenadas absolutas

# Valor alfa
etiqueta6 = tk.Label(root, text="Difusividad térmica x 10-6 m2/s", bg="yellow", font=("Arial", 10))
etiqueta6.place(x=50, y=260)  # Coordenadas absolutas

# Valor mu
etiqueta7 = tk.Label(root, text="Viscosidad absoluta x 10-6 Pa s", bg="yellow", font=("Arial", 10))
etiqueta7.place(x=50, y=290)  # Coordenadas absolutas

# Valor mu cinemática
etiqueta8 = tk.Label(root, text="Viscosidad cinemática x 10-6 m2/s", bg="yellow", font=("Arial", 10))
etiqueta8.place(x=50, y=320)  # Coordenadas absolutas

# Valor Pr
etiqueta9 = tk.Label(root, text="Número de Prandtl", bg="yellow", font=("Arial", 10))
etiqueta9.place(x=50, y=350)  # Coordenadas absolutas


# Cálculos de interpolación

tipo = 'cubic'

f_interp1 = interp1d(x, y1, kind=tipo)
f_interp2 = interp1d(x, y2, kind=tipo)
f_interp3 = interp1d(x, y3, kind=tipo)
f_interp4 = interp1d(x, y4, kind=tipo)
f_interp5 = interp1d(x, y5, kind=tipo)
f_interp6 = interp1d(x, y6, kind=tipo)
f_interp7 = interp1d(x, y7, kind=tipo)
f_interp8 = interp1d(x, y8, kind=tipo)
f_interp9 = interp1d(x, y9, kind=tipo)

# Nuevos puntos para interpolar
x_nuevo = np.linspace(min(x), max(x), 250)

y_nuevo1 = f_interp1(x_nuevo)
y_nuevo2 = f_interp2(x_nuevo)
y_nuevo3 = f_interp3(x_nuevo)
y_nuevo4 = f_interp4(x_nuevo)
y_nuevo5 = f_interp5(x_nuevo)
y_nuevo6 = f_interp6(x_nuevo)
y_nuevo7 = f_interp7(x_nuevo)
y_nuevo8 = f_interp8(x_nuevo)
y_nuevo8 = f_interp9(x_nuevo)

# Ejemplo: interpolar un valor específico
x_consulta = x

y_consulta1 = f_interp1(x_consulta)
y_consulta2 = f_interp2(x_consulta)
y_consulta3 = f_interp3(x_consulta)
y_consulta4 = f_interp4(x_consulta)
y_consulta5 = f_interp5(x_consulta)
y_consulta6 = f_interp6(x_consulta)
y_consulta7 = f_interp7(x_consulta)
y_consulta8 = f_interp8(x_consulta)
y_consulta8 = f_interp9(x_consulta)

resultado_label1 = tk.Label(root, text="Resultado", bg="cyan", font=("Arial", 10))
resultado_label1.place(x=320, y=110)  # Coordenadas absolutas

resultado_label2 = tk.Label(root, text="Resultado", bg="cyan", font=("Arial", 10))
resultado_label2.place(x=320, y=140)  # Coordenadas absolutas

resultado_label3 = tk.Label(root, text="Resultado", bg="cyan", font=("Arial", 10))
resultado_label3.place(x=320, y=170)  # Coordenadas absolutas

resultado_label4 = tk.Label(root, text="Resultado", bg="cyan", font=("Arial", 10))
resultado_label4.place(x=320, y=200)  # Coordenadas absolutas

resultado_label5 = tk.Label(root, text="Resultado", bg="cyan", font=("Arial", 10))
#resultado_label.pack(pady=1)
resultado_label5.place(x=320, y=230)  # Coordenadas absolutas

resultado_label6 = tk.Label(root, text="Resultado", bg="cyan", font=("Arial", 10))
resultado_label6.place(x=320, y=260)  # Coordenadas absolutas

resultado_label7 = tk.Label(root, text="Resultado", bg="cyan", font=("Arial", 10))
resultado_label7.place(x=320, y=290)  # Coordenadas absolutas

resultado_label8 = tk.Label(root, text="Resultado", bg="cyan", font=("Arial", 10))
resultado_label8.place(x=320, y=320)  # Coordenadas absolutas

resultado_label9 = tk.Label(root, text="Resultado", bg="cyan", font=("Arial", 10))
resultado_label9.place(x=320, y=350)  # Coordenadas absolutas

#tk.Button(root, text="Interpolar", command=calcular).pack()

btn3 = tk.Button(root, text="Interpolar", command=calcular)
btn3.place(x=200, y=60)  # Posición exacta

# Botón para limpiar
btn_limpiar = tk.Button(root, text="Limpiar", command=limpiar_entry)
btn_limpiar.place(x=270, y=60)  # Posición exacta
#btn_limpiar.pack(pady=10)




root.mainloop()