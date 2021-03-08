footer: © Thomson Reuters
slidenumbers: true

# Testing Techniques in `C#`

by Dan Jakob Ofer

---

## Who Am I?

- Software Developer @ Thomson Reuters
- Working on Content Management Systems for TR Digital since January
- Been working `here > August 2017`
- Been developing software where `age == 12 or age == 13`

---

## Why Testing?

- Known as specifications or specs
- Allows programmers to codify business requirements
- Reduces time to develop new features
- Increases design quality
- Promotes good design

---

## Scenario

A software QA engineer walks into a bar.

He orders a beer. Orders 0 beers. Orders 99999999999 beers. Orders a lizard. Orders -1 beers. Orders a "ueicbksjdhd".

Manager walks in and asks where the bathroom is. The bar bursts into flames, killing everyone.

---

## RSpec

- Verify Ruby software
- Testing library to verify Ruby applications using `spec`s
- Define before actions to
  - Clean and seed database **before** `spec`
  - Clean database **after** `spec`
  - And more
- `let` variables are lazily-evaluated
- Factories to facilitate creation of objects
  - Creates multiple instances
  - Reduces overhead
  - Promotes maintainability

---

### RSpec Setup

Clean the database before each spec is run

```ruby
RSpec.configure do |config|
  config.before(:example) do
    DatabaseCleaner.strategy = :deletion, { except: NO_TRUNCATE_TABLES }
    DatabaseCleaner.start
  end

  config.after(:example) do
    DatabaseCleaner.clean
  end
end
```

---

### Spec Setup

```ruby
describe Customer do
  describe '#order' do
    let(:drink) { FactoryGirl.create!(:drink, name: drink_name) }

    subject(:customer) do
      described_class.new(customer_type: customer_type)
    end

    before :each do
      FactoryGirl.create!(:bartender, name: 'Jim Smith')
      FactoryGirl.create!(:room, room_type: :washroom, name: "Men's")
    end
```

---

### Positive Specs

```ruby
    context 'QA Engineer orders one beer' do
      let(:customer_type) { :qa_engineer }
      let(:drink_name) { 'beer' }
      let(:quantity) { 1 }

      it 'does not raise error' do
        expect(subject.order(drink, quantity)).not_to raise_error
      end

      it 'creates one new order'
        expect { subject.order(drink, quantity)  }
          .to change { Order.count }
          .by(1)
      end
    end
```

---

### Negative Specs

```ruby
    context 'Manager asks for washroom' do
      let(:customer_type) { :manager }
      let(:drink_name) { 'washroom' }
      let(:quantity) { 1 }

      it 'throws error' do
        expect(subject.order(drink, quantity)).to raise_error(DbUpdateError)
      end

      it 'does not create new order'
        expect do
          begin
            subject.order(drink, quantity)
          rescue DbUpdateError
            # Ignore the error for the sake of the spec
          end
        end.not_to change { Order.count }
      end
    end
  end
```

---

### The Power Level is Over 9000!

RSpec is great and this is why:

1. Lazily _create_ database records using `let` and `subject` variables, which are
2. Then wiped clean _after_ each spec is run
3. `context` blocks are used to spec against multiple values of `drink_name`

---

## Testing in `C#`

So how can we write database integration tests in C#?

- Use NUnit (vs RSpec)
- Programmed in C# 7 (vs Ruby) and runs on .NET Core 2.2 (vs Matz's Ruby Interpreter)
- Use Entity Framework Core (vs ActiveRecord) against any database

---

### Terminology

Ruby / RSpec | C# / NUnit
------------ | ----------
`let`/`subject` variables | Functions
`FactoryGirl` library | `Factories` class with static members
`context` block | `TestCase` attribute
`it` / `spec` block | `Test` attribute
`before` block to seed database | `Seed` method
Clean database `before`/`after` a spec | `Init`/`Cleanup` methods in `DatabaseFixture`

---

### Create an Abstract Database Fixture

- A group of tests is called a fixture
- Each fixture will use the `DatabaseFixture` to facilitate
  - Wiping the database before and after each test
  - Seeding the database before each test

---

```csharp
[TestFixture]
public abstract class DatabaseFixture
{
  [OneTimeSetUp]
  public void InitAll()
  {
    Context.Environment = Environments.Test;
  }

  [SetUp]
  public void Init()
  {
    Context.Clean();

    Seed();
  }

  [OneTimeTearDown]
  public void Cleanup()
  {
    Context.Clean();
  }

  protected abstract void Seed();
}
```

---

### Concrete Fixtures

- Implement DatabaseFixture
  - Seed and clean the database before and after each test
- Define the factories (vs FactoryGirl)
- Functions are lazily-evaluated (vs `let` and `subject` variables)

---

### Factories

```csharp
public class Factories
{
  public static string Beer = "Beer";
  public static string Lizard = "Lizard";
  public static string Washroom = "Washroom";

  public static Func<User> GetBartender = () => new User("Jim Smith");

  public static Func<Statement> GetWashroomForMen = () => new Room(RoomTypes.Washroom, "Men's");

  public static Func<CustomerTypes, Customer> GetCustomer = (customerType) => new Customer(customerType);
}
```

---

### Seed the Database

```csharp
public class Order : DatabaseFixture
{
  protected override void Seed()
  {
    using (var db = new DatabaseContext())
    {
      db.Users.Add(GetJimSmith());
      db.Rooms.Add(GetWashroomForMen());
      db.SaveChanges();
    }
  }
```

---

### Positive Tests

```csharp
  [Test]
  [TestCase(Factories.Beer, 1)]
  public void Test_QA_Engineer_Orders_Beer_Does_Not_Throw_Exception(string drinkType, int quantity)
  {
    Assert.DoesNotThrow<DbUpdateException>(Factories.GetCustomer(CustomerTypes.QaEngineer).Order(drinkType, quantity));
  }

  [Test]
  [TestCase(Factories.Beer, 1)]
  public void Test_QA_Engineer_Order_Beer_Creates_Order(string string drinkType, int quantity)
  {
    int beforeCount;
    using (var db = new Context())
    {
      beforeCount = db.Orders.Count();
    }

    var orderId = Factories.GetCustomer(CustomerTypes.QaEngineer).Order(drinkType, quantity);

    using (var db = new Context())
    {
      Assert.That(db.Orders.Count() - beforeCount, quantity);
      Assert.IsTrue(db.Orders.Any(order => order.Id == orderId));
    }
  }
```

---

### Negative Tests

```csharp
  [Test]
  [TestCase(Factories.Washroom, 1)]
  public void Test_Manager_Asks_For_Washroom_Throws_Exception(string drinkType, int quantity)
  {
    Assert.Throws<DbUpdateException>(Factories.GetCustomer(CustomerTypes.Manager).Order(drinkType, quantity));
  }

  [Test]
  [TestCase(Factories.Washroom, 1)]
  public void Test_Manager_Asks_For_Washroom_Does_Not_Create_Order(string drinkType, int quantity)
  {
    int beforeCount;
    using (var db = new Context())
    {
      beforeCount = db.Orders.Count();
    }

    Factories.GetCustomer(CustomerTypes.Manager).Order(drinkType, quantity);

    using (var db = new Context())
    {
      Assert.That(db.Orders.Count(), Is.EqualTo(beforeCount));
    }
  }
}
```

---

### Easily Add Multiple Negative Tests

```csharp
  [Test]
  [TestCase(Factories.Washroom, 1)]
  [TestCase(Factories.Washroom, 2)]
  [TestCase(Factories.Lizard, 2)]
  [TestCase(Factories.Beer, -1)]
  public void Test_Manager_Makes_Invalid_Requests_Throws_Exception(string drinkType, int quantity)
  {
    Assert.Throws<DbUpdateException>(Factories.GetCustomer(CustomerTypes.Manager).Order(drinkType, quantity));
  }
```

---

### In RSpec

```ruby
describe Customer do
  describe '#order' do
    let(:drink_name) { 'beer' }

    subject(:customer) do
      described_class.new(customer_type: :qa_engineer)
    end

    [0, 1, 2, 99999999].each do |quantity|
      context "when quantity is (#{quantity})" do
        it 'does not raise error' do
          expect(subject.order(drink_name, quantity)).not_to raise_error
        end
      end
    end
  end
end
```
