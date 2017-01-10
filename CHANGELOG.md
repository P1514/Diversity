# Change Log
All notable changes to this project will be documented in this file.

##[Unreleased]
### 
- Integration with outside modules
- Code refactoring on latest added code (1.1.7+)

### Modeling
- Create and use Extra DB table to keep info of last time updated

### Extraction
- Snapshoting values to be saved on local DB
- Request Extrapolation using prediction
- Able to filter and see all products related to each PSS

### Prediction Model
- Everything

## [2.5] 10-01-2017

### Backend
- Various Fixes

### Modeling
- Date setting should now be fully implemented.

### Integration
- Polarity calculations can be taken out of this module

### Front-end
- New pop up windows.
- Various Fixes

## [2.3.1] 23-11-2016
###
- Minor Fixes

## [2.3] 2016-11-19
### Refactoring
- Program now able to store more data

### Extraction
- Final product filtering implemented

### Modeling
- Now can select final products from tree list that are supposed to be monitored

### Pages
- Home page Redesigned
- Model page Redesigned

### Security
- Access Rights Implemented


## [2.2] 2016-11-06
### Monitoring
- Monitoring Implemented 24h cycle
- Monitoring implemented with local and remote data possibilities
### Home
Top 5 Reach PSS chart displayed on Sentiment Analysis home page
### Modeling
- Modeling page now allows for start date and products filtering (due to DB limitations not implemented on back-end)
#### Opinion extraction:
-Option to filter by age
-Option to filter by final product (not implemented)

## [2.1] 2016-10-26
### Fixed
- Code Refactoring and Package Reorganization
- Backend Top5Reach working waiting for front-end for testing

## [2.0] 2016-10-19
### Added 
- Big Leap on Version since from now on Software both gets data from LocalDB but also gets it from a Specific Url(able to use models uri on a future release)

## [1.1.10] 2016-10-11
### Fixed
- OE page now shows colors as intended

## [1.1.9] 2016-10-10
## Added
- OE page fully Developed (Requires extensive tests)

## [1.1.8.6] 2016-10-10
### Added
- 4/6 Graphs showing filtering

## [1.1.8] 2016-10-04
### Added
- New Opinion Extraction Page Working
- Archiving Models

### Deprecated
- Multiple code pre-version 1.1.7 that is no longer needed
- In-Model Filtering Options

## [1.1.7.9] 2016-09-30
### Added
- New Opinion Extraction Page

## [1.1.7.1] 2016-09-23
### Fixed
- Full model compatibility

## [1.1.7] 2016-06-16
### Added
- Support for booting with models
- Program now loads everything on startup (no longer required first refresh)
- Code Optimization for Speed
- Graphs working with Model view (age range and gender still unimplemented)

## [1.1.6] 2016-06-16
### Added
- Model Page should now be fully supported
- Include final products option still unimplemented waiting for clarification on how it should be worked.
- This Version Requires DB Upate (Also added field to return Error in case DB version mismatch)
## [1.1.5] 2016-09-13
### Added
- Models Page new model Working
- Waiting for development of Update and Delete to add link in main page

## [1.1.4] 2016-09-06
### Added
- Chart Setup page as long as button on home screen to direct to that page
- Compatiblity of the new Setup capabilities with the current pages implemented (Opinion  and Global Sentiment) 

## [1.1.3] 2016-09-02
### Fixed
- Coerent graphic display
- Product Names

## [1.1.2] 2016-09-01
### Optimization
- Multi-Threading implementeded

## [1.1.1] 2016-08-03
### Added
- PSS info on all pages

## [1.1] 2016-08-29
### Added
- Add filter info to graph label
- Chart for the population distribution

## [1.0.9] 2016-07-28
### Added New DB.sql Import needed
- Loading ready for over-time import
- Loading set to month to month to help with influence calculations
- Influence Reach page with last post and only opinion authors
- Bug fixes

## [1.0.6] 2016-07-27 Minor Fix on 2016-07-28
### Added/Fixed
- Click to see associated comments(Both Pages POP-UP)
- Click to see Opinions at that time(Global Sentiment)
- Home button redesign
- Some font changes
- Now allows different TomCat Ports because of Compatibility issues

## [1.0.5.2] 2016-07-27
### Added
- Click to see Opinions at that time(Global Sentiment)
- Home button redesign
- Some font changes
- Now allows different TomCat Ports because of Compatibility issues

## [1.0.5.1] 2016-07-22
### Fixed
- Change East,West to Asia,Europe Waiting for Sim to Test
- Replace "Post" with "Top 5"
- Change "Chart" to Product 
- Delete Product Label
- Change "Sentiment" to "Polarity" (get opinion page)
- Set global sentiment graph to one month ahead
- Change name and location of "Back" button to "Home" and top left corner
- Keep product info when going back to main page
- Change and reformat radio buttons on get opinion page

## [1.0.5] 2016-07-22
### Added
- Globalsentiment Table
- Filter By Products
- Tested with OpinionSimV250

## [1.0.4] 2016-07-21
### Added/Fixed
- Globalsentiment fully functional(Missing only Post Table)
- Opinion Extraction fully functional
- Updated to work with OpinionSimV230(250 incoming)

## [1.0.3] 2016-07-20
### Added
- Multi-Threading
- Sentiment Graph and Table

## [1.0.2.2] 2016-07-19
### Added/Fixed
- Added DB parameters, reload DB before running current version
- Output Top Opinions Max 5 HardCored
- Max Decimal places reduced to 2 on all pages

## [1.0.2.1] - 2016-07-19
### Fixed
- Resource Management Optimized
- Simplified Workspace Organization

## [1.0.2] - 2016-07-18
### Added
- This CHANGELOG file.
- README now contains setup instruction and known issues.
- Project now run all from eclipse without needing to navigate outside
- Globalsentiment page Chart working
- Fixed import issues [To Be Confirmed]
