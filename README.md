# QNQ & Shopify Taxi Simulator

Taxi-Simulator is a simulation of a cheap taxi service offered by supermarket chains QnQ and Shopify using a graph data structure.

## Description of Features

- Given an incoming call, TaxiSimulator calculates and identifies the taxi that can make the lowest cost trip to the client and then to the nearest shop.
- Added different shop and taxi types to the simulation - Namely QnQ and Shopify.
- Restricted Taxis to operate between shops of their own company.
- Allow the user to decide which company they want a taxi from - display cannot be helped if not possible.
- Added functionality to calculate the fare owed by the client, based on the following criteria:
  - The Booking Price for QnQ Taxis is R14.50 and Shopify Taxis is R16.
  - Clients Pay 20% of the Pick-Up Cost for QnQ Taxis and 15% for Shopify Taxis for driver petrol.
  - Clients Pay 100% of the Drop-Off Cost for both QnQ and Shopify Taxis.
- Drivers will randomly decline client call with a 30% probability, resulting in varying results which simulate real-world conditions.

## To Run the TaxiSimulator Class

```
touch Input.txt
make compile
make run
```

## Input Format

**Conditions** - There must be at least 3 nodes for the Simulation to run, as each Simulation must have at least one client, QnQ Shop and Shopify Shop.

<pre>
&lt;number of nodes&gt;&lt;newline&gt;
{&lt;source node number&gt; {&lt;destination node number&gt; &lt;weight&gt;}&lt;newline&gt;}
&lt;number of QnQ shops&gt;&lt;newline&gt;
{&lt;QnQ shop node numbers&gt;}&lt;newline&gt;
&lt;number of Shopify shops&gt;&lt;newline&gt;
{&lt;Shopify shop node numbers&gt;}&lt;newline&gt;
&lt;number of clients&gt;&lt;newline&gt;
{&lt;client node number&gt; &lt;client node preferred company&gt;}*&lt;newline&gt;
</pre>

## Example Input and Output

### Case 1: All Clients Helped.

#### Input:

<pre>
7
0 1 20 3 7 2 5
1 3 8 5 3
2 3 6
3 4 15 6 10
4 5 1
5 6 17
6
2
2 4 0
2
1 6
2
3 QnQ 5 Shopify
</pre>

#### Expected Output:

<pre>
client 3
company qnq
taxi 2
2 3
shop 4
3 4
amount due for this client is R30.70
client 5
company shopify
taxi 1
1 5
shop 6
5 6
amount due for this client is R33.45
</pre>
