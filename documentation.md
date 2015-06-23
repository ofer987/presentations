# Documentation
## A Talk

By Dan Jakob Ofer (theScore)

Presented at theScore on Wednesday, June 24<sup>th</sup>, 2015
<br>
![inline 70%](images/the_score_logo.png)

Slides uploaded at [http://www.danofer.com/presentations/documentation](http://www.danofer.com/presentations/documentation)

---

# What is Documentation?

- Written by the author of the code
- To teach **you** how to use the code

---

# Install It:

## Ruby

- `rvm docs generate` (https://rvm.io/rubies/docs)

## Ruby gem

- `gem install gem_name --document`

---

# ri

- `ri ClassName.MethodName`
- `ri ClassName`
- `ri MethodName`

- E.g., ri Array
- E.g., ri Object.method

---

# How to Read a Ruby Program

- Method calls are resolved at runtime.
- Use the Object#method call to learn:
   - source_location
   - owner
   - source

---

# Pry

- **REPL**: Read - Evaluate - Parse - Loop
- Type `binding.pry` statement in Ruby application to enter pry

## Then type
- `help` to learn how to use it
- `ls object_name` to read methods on object
- `cd object_name` to change context into object

---

# How to Install Pry

- To use the `binding.pry` statement pry:
  - Install the [pry-byebug gem](https://github.com/deivid-rodriguez/pry-byebug)
- To use pry with `Rails console`:
  - Install the [pry-rails gem](https://github.com/rweng/pry-rails)

---

# Read the gem

- Use `bundle open gem_name` to open gem code in text editor
- Use `bundle show gem_name` to know version installed and where

---

# Git

- Use `git blame file_name` for authorship
- Use `git reflog` to view list of states and `git reset` to change state

---

# Other documentation tools

- Dash [kapeli.com/dash](https://kapeli.com/dash) for 19.99 USD
- Google [www.google.com](http://www.google.com)
- Ruby on Rails API [api.rubyonrails.org](http://api.rubyonrails.org)
- The *ever invaluable* Ruby on Rails Guides [guides.rubyonrails.org](http://guides.rubyonrails.org)

---

# You can find these slides at [http://www.danofer.com/presentations/documentation](http://www.danofer.com/presentations/documentation)
